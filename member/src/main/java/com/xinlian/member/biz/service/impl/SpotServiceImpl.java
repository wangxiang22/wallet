package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.*;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.*;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.CheckPwdReq;
import com.xinlian.common.request.OrderStateReq;
import com.xinlian.common.request.SellCatReq;
import com.xinlian.common.request.SureBuyReq;
import com.xinlian.common.response.OrderOpenRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.scheduling.SpotLogSave;
import com.xinlian.member.biz.service.*;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.xinlian.common.contants.OrderConstant.*;
import static com.xinlian.common.enums.CurrencyEnum.*;
import static com.xinlian.common.enums.WalletTradeOrderStatusEnum.TRADE_SUCCESS;

@Slf4j
@Service
public class SpotServiceImpl implements SpotService {
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TWalletTradeOrderService tWalletTradeOrderService;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TOrderInfoService tOrderInfoService;
    @Autowired
    private SpotLogSave spotLogSave;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;

    /**
     * 1. 检查支付密码
     * 2。检查余额是否充足
     * 3. 扣除余额加入到冻结
     * 4. 生成订单
     * 5. 用户资金冻结加入记录
     *
     * @param sellCatReq
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseResult sellCat(SellCatReq sellCatReq) {
        //查出用户信息校验密码
        TUserInfo sellerUserInfo = this.checkSellerUserInfo(sellCatReq);
        //验证地址，是否存在系统中-而不是外部地址
        Long buyerUid = this.getUidByCurrencyAddress(sellCatReq.getAddress());
        //查看买家是否冻结
        TUserInfo buyerUserInfo = tUserInfoMapper.selectById(buyerUid);
        if(UserLevelStatusEnum.FREEZE.getCode() == buyerUserInfo.getLevelStatus()){
            throw new BizException("对方账户异常，请核实!");
        }
        //1.校验验证码
        String phone = sellCatReq.getCountryCode()==86 ? sellCatReq.getPhone() : sellCatReq.getCountryCode()+sellCatReq.getPhone();
        log.info("sell CAT countryCode:{},phone:{}",sellCatReq.getCountryCode(),sellCatReq.getPhone());
        String smsCodeRedisKey = this.doCheckSellCatCode(sellCatReq,phone);
        //2 check 智能合约交易双方开发
        this.checkNodePromissory(sellCatReq.getUid(),buyerUid);
        //挂单限制只能挂1条
        TOrder dbOrder = tOrderService.selectOne(new EntityWrapper<TOrder>().eq("state", 0).eq("seller_uid", sellCatReq.getUid()));
        if (dbOrder != null) {
            throw new BizException(ORDER_IS_ONLINE);
        }
        //3.冻结出售用户CAT钱包 、CAG钱包
        int resultCat = this.freezeWalletToCAT(sellCatReq);
        int resultCag = this.freezeWalletToCAG(sellCatReq);
        if(0==resultCag || 0==resultCat){
            throw new BizException(BALANCE_NOT_ENOUGH);
        }
        //添加订单
        TOrder tOrder = this.createSmartContractOrder(sellCatReq,sellerUserInfo,buyerUserInfo);
        boolean insert = tOrderService.insert(tOrder);
        if (!insert) {
            throw new BizException(MAKE_ORDER_ERROR);
        }
        //客户CAT资金冻结加入记录
        TWalletTradeOrder catTraderOrder = this.createWalletTradeOrderCAT(sellCatReq,buyerUserInfo);
        if (1!=tWalletTradeOrderMapper.insert(catTraderOrder)) {
            throw new BizException(MAKE_ORDER_ERROR);
        }
        //cag冻结加入记录
        TWalletTradeOrder feeWalletTradeOrder = this.createWalletTradeOrderCag(sellCatReq,catTraderOrder.getId());
        if (1!=tWalletTradeOrderMapper.insert(feeWalletTradeOrder)) {
            throw new BizException(MAKE_ORDER_ERROR);
        }
        //组装涉及ids
        String involveTradeOrderIds = "CAT:".concat(catTraderOrder.getId().toString()).concat("|CAG_FEE:").concat(feeWalletTradeOrder.getId().toString());
        spotLogSave.addLog(sellCatReq.getUid(),tOrder.getOrderId(),involveTradeOrderIds,"SELL");
        //走到最后一步再删除
        redisClient.deleteByKey(smsCodeRedisKey);
        return ResponseResult.ok(tOrder.getOrderId());
    }

    private TUserInfo checkSellerUserInfo(SellCatReq sellCatReq){
        TUserInfo sellerUserInfo = tUserInfoMapper.selectById(sellCatReq.getUid());
        if(sellerUserInfo == null){
            throw new BizException("用户信息有误");
        }
        if(sellerUserInfo.getLevelStatus() == 0){
            throw new BizException("用户已被冻结");
        }
        String pwd = EncryptionUtil.md5Two(sellCatReq.getPayPassword(), sellerUserInfo.getSalt());
        if (!pwd.equals(sellerUserInfo.getPayPassWord())) {
            throw new BizException(PAY_PASSWORD_ERROR);
        }
        sellCatReq.setPhone(sellerUserInfo.getMobile());
        sellCatReq.setCountryCode(sellerUserInfo.getCountryCode());
        return sellerUserInfo;
    }

    private int freezeWalletToCAT(SellCatReq sellCatReq) {
        //查出用户CAT余额
        TWalletInfo whereWalletInfo = new TWalletInfo();
        whereWalletInfo.setUid(sellCatReq.getUid());
        whereWalletInfo.setCurrencyId(Long.valueOf(CAT.getCurrencyId()));//cat
        TWalletInfo walletResult = tWalletInfoMapper.selectOne(whereWalletInfo);
        //检查余额是否充足
        if (walletResult.getBalanceNum().compareTo(sellCatReq.getAmount()) == -1) {
            throw new BizException(BALANCE_NOT_ENOUGH);
        }
        //卖家地址
        sellCatReq.setSellerAddr(walletResult.getCurrencyAddress());
        //冻结余额
        sellCatReq.setCurrencyId(CAT.getCurrencyId());
        return tWalletInfoMapper.frozen(sellCatReq);
    }

    private BigDecimal getCagFee(SellCatReq sellCatReq){
        //计算cag手续费
        BigDecimal cagFee ;
        if (sellCatReq.getAmount().compareTo(new BigDecimal(1)) < 1) {//小于等于1手续费2cag
            cagFee = new BigDecimal(2);
        } else if (sellCatReq.getAmount().compareTo(new BigDecimal(10)) > -1) {//大于等于20书续费20cag
            cagFee = new BigDecimal(20);
        } else {
            cagFee = sellCatReq.getAmount().multiply(new BigDecimal(2)).setScale(4, BigDecimal.ROUND_DOWN);
        }
        return cagFee;
    }

    private int freezeWalletToCAG(SellCatReq sellCatReq) {
        //获取出售CAT - CAG的手续费
        BigDecimal cagFee = this.getCagFee(sellCatReq);
        TWalletInfo cagWallet = new TWalletInfo();
        cagWallet.setUid(sellCatReq.getUid());
        cagWallet.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
        TWalletInfo cagWalletDbResult = tWalletInfoMapper.selectOne(cagWallet);
        if (cagWalletDbResult.getBalanceNum().compareTo(cagFee)==-1){
            throw new BizException(FEE_NOT_ENOUGH);
        }
        //冻结手续费
        SellCatReq cagFeeModel = new SellCatReq();
        cagFeeModel.setAmount(cagFee);
        cagFeeModel.setCurrencyId(CAG.getCurrencyId());
        cagFeeModel.setUid(sellCatReq.getUid());
        return tWalletInfoMapper.frozen(cagFeeModel);
    }

    private String doCheckSellCatCode(SellCatReq sellCatReq,String phoneByDB) {
        String redisCode = "";
        String redisKey = SmsUtil.createPhoneKey(phoneByDB).concat("_CAT_SELL");
        if (!StringUtils.isEmpty(phoneByDB)) {
            redisCode = redisClient.get(redisKey);
            if (StringUtils.isEmpty(redisCode)) {
                throw new BizException("请发送验证码后再试");
            }
            log.info("用户phonerediscode:{}", redisCode);
            log.info(sellCatReq.toString());
            if (redisCode == null || !redisCode.equals(sellCatReq.getCode())) {
                checkSmsRuleHandler.doSaveSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.SELL_CAT.getMethodCode());
                throw new BizException("验证码错误");
            }
            checkSmsRuleHandler.doDeleteSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.SELL_CAT.getMethodCode());
            //redisClient.deleteByKey(SmsUtil.createPhoneKey(phoneByDB) + "_CAT_SELL");
            return redisKey;
        } else {
            throw new BizException("请发送验证码后再试");
        }
    }

    private TWalletTradeOrder createWalletTradeOrderCAT(SellCatReq sellCatReq,TUserInfo buyerUserInfo) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(sellCatReq.getUid());
        tWalletTradeOrder.setCurrencyId(Long.valueOf(sellCatReq.getCurrencyId()));
        tWalletTradeOrder.setCurrencyCode(CAT.getCurrencyCode());
        tWalletTradeOrder.setTradeType(WalletTradeTypeEnum.CAT_PENDING_ORDER.getTradeType());
        tWalletTradeOrder.setDes(WalletTradeTypeEnum.CAT_PENDING_ORDER.getTradeDesc());
        //交易-对方信息
        tWalletTradeOrder.setCounterPartyUid(buyerUserInfo.getUid());
        tWalletTradeOrder.setCounterPartyUserName(buyerUserInfo.getUserName());
        tWalletTradeOrder.setCounterPartyMobile(buyerUserInfo.getMobile());
        tWalletTradeOrder.setTradeCurrencyNum(sellCatReq.getAmount().abs().multiply(new BigDecimal(-1)));
        tWalletTradeOrder.setTradeAddress(sellCatReq.getAddress());
        tWalletTradeOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        tWalletTradeOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        tWalletTradeOrder.setCreateTime(new Date());
        return tWalletTradeOrder;
    }

    private TWalletTradeOrder createWalletTradeOrderCag(SellCatReq sellCatReq, @NotNull Long catTradeOrderId) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(sellCatReq.getUid());
        tWalletTradeOrder.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
        tWalletTradeOrder.setCurrencyCode(CAG.getCurrencyCode());
        tWalletTradeOrder.setTradeType(WalletTradeTypeEnum.CAT_PENDING_ORDER_CAG_FEE.getTradeType());
        tWalletTradeOrder.setDes(WalletTradeTypeEnum.CAT_PENDING_ORDER_CAG_FEE.getTradeDesc());
        //tWalletTradeOrder.setRemark("智能合约手续费依据订单ID:".concat(catTradeOrderId.toString()));
        //这里是CAT手续费 -cag
        BigDecimal cagFee = this.getCagFee(sellCatReq);
        tWalletTradeOrder.setTradeCurrencyNum(UdunBigDecimalUtil.convertMinusJudgeZero(cagFee));
        //tWalletTradeOrder.setTradeAddress(sellCatReq.getAddress());
        tWalletTradeOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        tWalletTradeOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        tWalletTradeOrder.setCreateTime(new Date());
        return tWalletTradeOrder;
    }

    private TOrder createSmartContractOrder(SellCatReq sellCatReq,
                                            TUserInfo sellerUserInfo,TUserInfo buyerUserInfo) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        TOrder tOrder = new TOrder();
        tOrder.setAmount(sellCatReq.getAmount());
        tOrder.setPrice(sellCatReq.getPrice());
        tOrder.setTotal(sellCatReq.getTotal());
        tOrder.setCreateTime(System.currentTimeMillis());
        tOrder.setBuyerAddr(sellCatReq.getAddress());
        tOrder.setState(OrderStateEnum.APPLY.getCode());
        tOrder.setOrderId(orderId);
        //售出cat - cag 费用
        tOrder.setCagFee(this.getCagFee(sellCatReq));
        //seller
        tOrder.setSellerUid(sellerUserInfo.getUid());
        tOrder.setSellerAddr(sellCatReq.getSellerAddr());
        tOrder.setSellerUsername(sellerUserInfo.getUserName());
        tOrder.setSellerPhone(sellerUserInfo.getMobile());
        //buyer
        tOrder.setBuyerUid(buyerUserInfo.getUid());
        tOrder.setBuyerAddr(sellCatReq.getAddress());
        tOrder.setBuyerUsername(buyerUserInfo.getUserName());
        tOrder.setBuyerPhone(buyerUserInfo.getMobile());
        return tOrder;
    }


    /**
     * 查询订单状态
     * type1 买  type2卖
     *
     * @param orderStateReq
     * @return
     */
    @Override
    public ResponseResult findOrderState(OrderStateReq orderStateReq) {
        if (orderStateReq.getType() == 2) {
            TOrder tOrder = tOrderService.queryOneByUid(orderStateReq);
            if (tOrder == null) {
                return ResponseResult.ok();
            }
            OrderOpenRes orderOpenRes = null;
            try {
                orderOpenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("配置有误");
            }
            String orderTimeOut = orderOpenRes.getOrderTimeOut();
            log.info("订单超时时间:" + orderTimeOut);
            long orderTimeOutL = Long.parseLong(orderTimeOut);
            tOrder.setTimeOutTime(tOrder.getCreateTime() + orderTimeOutL);
            return ResponseResult.ok(tOrder);
        }
        return null;
    }

    /**
     * 购买cat
     * 1.校验验证码git
     * 2.余额是否充足
     * 3.订单是否超时/订单状态是否是待成交
     * 4.购入成功，卖家冻结金额扣除，增加usdt，买家增加相应cat
     * 5.卖家更新 记录 +xxxusdt
     * 6.买家更新 +xxxcat -xxxusdt
     * 7.更新订单
     *
     * @param sellCatReq
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult buyCat(SellCatReq sellCatReq) {
        //查出用户状态 信息校验支付密码
        TUserInfo buyerUserInfo = this.checkUserInfoStateAndPaypwd(sellCatReq);
        //校验验证码
        String phoneSmsCodeRedisKey = this.checkBuyCatPhoneSmsCode(sellCatReq,buyerUserInfo);
        //订单是否超时 or 订单状态是否是待成交
        TOrder tOrder = this.getStayTradeOrder(sellCatReq.getOrderId());
        //检验买方 余额是否充足
        TWalletInfo dbWallet = this.checkBuyerBalanceIsMuch(tOrder.getTotal());
        //check buyer 是否有资格
        this.checkBuyerIsQualification(tOrder.getBuyerAddr(),dbWallet.getCurrencyAddress());
        //购买cat手续费是否充足
        this.checkBuyerBalanceIsCagFee(tOrder.getCagFee());
        //验证订单超时
        this.checkOrderTimeOut(tOrder);
        //校验节点信息
        this.checkNodePromissory(tOrder.getSellerUid(),jwtUtil.getUserId(httpServletRequest));
        //购入成功，卖家冻结金额扣除，增加usdt，买家增加相应cat 双方-手续费
        this.orderTrading(tOrder);
        //更新钱包流水记录
        List<TWalletTradeOrder> list = this.addList(tOrder);
        boolean batchFlag = tWalletTradeOrderService.insertBatch(list);
        if (!batchFlag) {
            throw new BizException(ORDER_ERROR);
        }
        //组装参数
        TOrder stayUpdateOrder = this.packageOrder(tOrder,sellCatReq);
        //更新订单
        EntityWrapper<TOrder> entityWrapper = new EntityWrapper<TOrder>();
        entityWrapper.eq("id",tOrder.getId());
        entityWrapper.eq("state",OrderStateEnum.APPLY.getCode());
        boolean b1 = tOrderService.update(stayUpdateOrder,entityWrapper);
        if (!b1) {
            throw new BizException(ORDER_ERROR);
        }
        //更新订单详情
        this.insertToOrderInfo(tOrder);
        String involveTradeOrderIds = this.packageInvolveTradeOrderIds(list);
        spotLogSave.addLog(sellCatReq.getUid(), tOrder.getOrderId(),involveTradeOrderIds,"BUY");
        //账户大额变动流水记录
        this.saveWithdrawTradeSuccessLog(tOrder);
        //全部走完再删除 phoneSmsCode
        redisClient.deleteByKey(phoneSmsCodeRedisKey);
        return ResponseResult.ok();
    }

    private void saveWithdrawTradeSuccessLog(TOrder tOrder) {
        //账户大额变动流水记录
        String uuid = UniqueNoUtil.uuid();
        //卖家：卖出cat【CAT挂单冻结】（卖家减CAT流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getSellerUid(),Long.parseLong(String.valueOf(CAT.getCurrencyId())), CAT.getCurrencyCode(),
                tOrder.getBuyerUid(), tOrder.getBuyerAddr(),tOrder.getAmount().abs().multiply(new BigDecimal("-1")),WalletTradeTypeEnum.CAT_PENDING_ORDER.getTradeDesc(),
                null, new Date(tOrder.getEndTime()),uuid);
        //卖家：卖出CAT所得（卖家加USDT流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getSellerUid(),Long.parseLong(String.valueOf(USDT.getCurrencyId())), USDT.getCurrencyCode(),
                tOrder.getBuyerUid(), tOrder.getBuyerAddr(),tOrder.getTotal().abs(),WalletTradeTypeEnum.SELL_CAT_INCOME.getTradeDesc(),null,new Date(tOrder.getEndTime()),uuid);
        //卖家：挂单手续费-CAG冻结（成功后就直接扣除，卖家减CAG手续费流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getSellerUid(),Long.parseLong(String.valueOf(CAG.getCurrencyId())), CAG.getCurrencyCode(),
                null,null,tOrder.getCagFee().abs().multiply(new BigDecimal("-1")),WalletTradeTypeEnum.CAT_PENDING_ORDER_CAG_FEE.getTradeDesc(),
                null, new Date(tOrder.getEndTime()),uuid);
        //买家：买入cat（买家加CAT流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getBuyerUid(),Long.parseLong(String.valueOf(CAT.getCurrencyId())), CAT.getCurrencyCode(),
                tOrder.getSellerUid(),tOrder.getSellerAddr(),tOrder.getAmount().abs(),WalletTradeTypeEnum.BUY_CAT_BUYER_ADD_CAT.getTradeDesc(),null,new Date(tOrder.getEndTime()),uuid);
        //买家：买入cat（买家减USDT流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getBuyerUid(),Long.parseLong(String.valueOf(USDT.getCurrencyId())), USDT.getCurrencyCode(),
                tOrder.getSellerUid(),tOrder.getSellerAddr(),tOrder.getTotal().abs().multiply(new BigDecimal("-1")),WalletTradeTypeEnum.BUY_CAT_BUYER_SUBTRACT_USDT.getTradeDesc(),
                null, new Date(tOrder.getEndTime()),uuid);
        //买家：兑入cat手续费（买家减CAG手续费流水监控）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tOrder.getBuyerUid(),Long.parseLong(String.valueOf(CAG.getCurrencyId())), CAG.getCurrencyCode(),
                null,null,tOrder.getCagFee().abs().multiply(new BigDecimal("-1")),WalletTradeTypeEnum.BUY_CAT_CAG_FEE.getTradeDesc(),
                null, new Date(tOrder.getEndTime()),uuid);
    }

    private String packageInvolveTradeOrderIds(List<TWalletTradeOrder> list){
        StringBuffer sb = new StringBuffer();
        //+USDT:-USDT:+CAT -CAG
        sb.append("SELL+U:").append(list.get(0).getId());
        sb.append("|BUY-U:").append(list.get(1).getId());
        sb.append("|BUY+CAT:").append(list.get(2).getId());
        sb.append("|BUY-CAG:").append(list.get(3).getId());
        return sb.toString();
    }

    private TUserInfo checkUserInfoStateAndPaypwd(SellCatReq sellCatReq){
        TUserInfo tUserInfo = tUserInfoMapper.selectById(this.getCurrentLoginUid());
        if (tUserInfo==null){
            throw new BizException("用户不存在");
        }
        if(UserLevelStatusEnum.FREEZE.getCode() == tUserInfo.getLevelStatus()){
            throw new BizException("账户已被冻结，请联系客服处理!");
        }
        String pwd = EncryptionUtil.md5Two(sellCatReq.getPayPassword(), tUserInfo.getSalt());
        if (!pwd.equals(tUserInfo.getPayPassWord())) {
            throw new BizException(PAY_PASSWORD_ERROR);
        }
        return tUserInfo;
    }

    private TOrder packageOrder(TOrder tOrder,SellCatReq sellCatReq){
        tOrder.setBuyerUid(sellCatReq.getUid());
        tOrder.setState(2);
        tOrder.setEndTime(System.currentTimeMillis());
        tOrder.setBuyerUsername(sellCatReq.getUsername());
        tOrder.setBuyerPhone(sellCatReq.getPhone());
        tOrder.setBuyerAddr(sellCatReq.getAddress());
        return tOrder;
    }

    private Long getCurrentLoginUid(){
        return jwtUtil.getUserId(httpServletRequest);
    }

    private void checkBuyerBalanceIsCagFee(BigDecimal buyCatToCatFee){
        TWalletInfo cagWallet = new TWalletInfo();
        cagWallet.setUid(this.getCurrentLoginUid());
        cagWallet.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
        TWalletInfo cagDbResult = tWalletInfoMapper.selectOne(cagWallet);
        if (cagDbResult == null) {
            throw new BizException(WALLET_ERROR);
        }
        if (cagDbResult.getBalanceNum().compareTo(buyCatToCatFee) == -1) {
            throw new BizException(FEE_NOT_ENOUGH);
        }
    }

    private TWalletInfo checkBuyerBalanceIsMuch(BigDecimal buyCatToUsdtCost){
        //余额是否充足
        TWalletInfo tWalletInfo = new TWalletInfo();
        tWalletInfo.setUid(this.getCurrentLoginUid());
        tWalletInfo.setCurrencyId(Long.valueOf(USDT.getCurrencyId()));
        TWalletInfo dbWallet = tWalletInfoMapper.selectOne(tWalletInfo);
        if (dbWallet == null) {
            throw new BizException(WALLET_ERROR);
        }
        if (dbWallet.getBalanceNum().compareTo(buyCatToUsdtCost) == -1) {
            throw new BizException(BALANCE_NOT_ENOUGH);
        }
        return dbWallet;
    }

    private TOrder getStayTradeOrder(String orderId){
        TOrder tOrder = tOrderService.selectOne(new EntityWrapper<TOrder>().eq("order_id", orderId));
        if (tOrder==null){
            throw new BizException(ORDER_IS_MISSING);
        }
        if (tOrder.getState() == 1) {
            throw new BizException(ORDER_TIME_OUT);
        } else if (tOrder.getState() == 2) {
            throw new BizException(ORDER_IS_TRADED);
        }
        return tOrder;
    }
    //Whether the purchase and sale are consistent
    private void checkBuyerIsQualification(@NotNull final String orderAddress,@NotNull final String buyerWallerInfoAddress){
        if(!orderAddress.equalsIgnoreCase(buyerWallerInfoAddress)){
            throw new BizException(ORDER_NO_PROMISSION);
        }
    }

    private String checkBuyCatPhoneSmsCode(SellCatReq sellCatReq,TUserInfo tUserInfo){
        sellCatReq.setPhone(tUserInfo.getMobile());
        sellCatReq.setCountryCode(tUserInfo.getCountryCode());
        String phone = sellCatReq.getCountryCode()==86 ? sellCatReq.getPhone() : sellCatReq.getCountryCode()+sellCatReq.getPhone();
        String redisCode = "";
        String phoneSmsCodeRedisKey = SmsUtil.createPhoneKey(phone) + "_TRADE_BUY";
        redisCode = redisClient.get(phoneSmsCodeRedisKey);
        if (StringUtils.isEmpty(redisCode)) {
            throw new BizException("请发送验证码后再试");
        }
        log.info("用户: phoneRedisCode:{}", redisCode);
        if (redisCode == null || !redisCode.equals(sellCatReq.getCode())) {
            checkSmsRuleHandler.doSaveSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.BUY_CAT.getMethodCode());
            throw new BizException("验证码错误");
        }
        checkSmsRuleHandler.doDeleteSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.BUY_CAT.getMethodCode());
        //redisClient.deleteByKey(SmsUtil.createPhoneKey(phone) + "_TRADE_BUY");
        return phoneSmsCodeRedisKey;
    }

    private void checkNodePromissory(@NotNull Long sellerUid,@NotNull Long buyerUid) {
        TOrder checkNodeParam = new TOrder();
        checkNodeParam.setBuyerUid(buyerUid);
        checkNodeParam.setSellerUid(sellerUid);
        this.checkNodePromissory(checkNodeParam);
    }

    private void checkNodePromissory(TOrder tOrder) {
        //0关1开
        String isCheck = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SPOT_NODE_TRADE.getBelongsSystemCode());
        if (isCheck.trim().equals("0")){
            return;
        }
        Long sellerUid = tOrder.getSellerUid();
        Long buyerUid = tOrder.getBuyerUid();
        TUserInfo sellerInfo = tUserInfoMapper.selectById(sellerUid);
        TUserInfo buyerInfo = tUserInfoMapper.selectById(buyerUid);
        Long sellerNodeId = sellerInfo.getServerNodeId();
        Long buyerNodeId = buyerInfo.getServerNodeId();
        if (sellerNodeId.longValue()==buyerNodeId.longValue()){
            return;
        }
        //买卖家节点信息
        TServerNode sellerServerNode = serverNodeService.getById(sellerNodeId);
        TServerNode buyerServerNode = serverNodeService.getById(buyerNodeId);
        //如果卖家节点没上级 直接拉出买家节点所有上级比较
        if (sellerServerNode.getParentId()==0){
            String parentIds = buyerServerNode.getParentIds();
            String[] split = parentIds.split(",");
            for (String s : split) {
                //买家所有上级和卖家节点比较
                if (s.trim().equals(sellerServerNode.getId().toString())){
                    return;
                }
            }
            throw new BizException("您与设定的兑出方/兑入方节点不一致，无法兑入/兑出,仅支持节点内互兑");
        }else {//如果卖家有上级拉出所有的上级和买家所有的上级比较
            String parentIds = sellerServerNode.getParentIds();
            String[] sellerIdsArr = parentIds.split(",");
            for (String s : sellerIdsArr) {
                //如果卖家上级就是买家当前节点直接通过
                if (s.trim().equals(buyerNodeId.toString())){
                    return;
                }
                String buyerIds = buyerServerNode.getParentIds();
                String[] buyerIdsArr = buyerIds.split(",");
                //如果卖家上级和买家上级一样也通过
                for (String s1 : buyerIdsArr) {
                    if (!s.trim().equals("0")){
                        if (s.trim().equals(s1.trim())){
                            return;
                        }
                    }
                }
            }
            throw new BizException("您与设定的兑出方/兑入方节点不一致，无法兑入/兑出,仅支持节点内互兑");
        }

    }

    private void insertToOrderInfo(TOrder tOrder) {
        Long sellerUid = tOrder.getSellerUid();
        //查询卖家  cat和usdt余额
        List<TWalletInfo> sellerList = tWalletInfoMapper.queryAccount(sellerUid);
        Long buyerUid = tOrder.getBuyerUid();
        List<TWalletInfo> buyerList = tWalletInfoMapper.queryAccount(buyerUid);
        List<TOrderInfo> tOrderInfoList = new ArrayList<>();
        //add卖家记录
        TOrderInfo sellerOrderInfo = new TOrderInfo();
        pushToOrderInfo(tOrder, sellerOrderInfo, 1);//填充tOrderinfo
        addBalance(sellerList, tOrderInfoList, sellerOrderInfo);
        //add买家记录
        TOrderInfo buyerOrderInfo = new TOrderInfo();
        pushToOrderInfo(tOrder, buyerOrderInfo, 0);//填充tOrderinfo
        addBalance(buyerList, tOrderInfoList, buyerOrderInfo);
        //把两个组装成orderInfo
        boolean b = tOrderInfoService.insertBatch(tOrderInfoList);
        if (!b) {
            throw new BizException(ORDER_ERROR);
        }
    }

    private void addBalance(List<TWalletInfo> buyerList, List<TOrderInfo> tOrderInfoList, TOrderInfo orderInfo) {
        for (TWalletInfo tWalletInfo : buyerList) {
            if (tWalletInfo.getCurrencyId() == CAT.getCurrencyId()) {
                orderInfo.setCat(tWalletInfo.getBalanceNum());
            }
            if (tWalletInfo.getCurrencyId() == USDT.getCurrencyId()) {
                orderInfo.setUsdt(tWalletInfo.getBalanceNum());
            }
        }
        tOrderInfoList.add(orderInfo);
    }

    private void pushToOrderInfo(TOrder tOrder, TOrderInfo tOrderInfo, int type) {
        if (type == 1) {//如果是卖
            tOrderInfo.setAmount(tOrder.getAmount());
            tOrderInfo.setEndTime(tOrder.getEndTime());
            tOrderInfo.setUsername(tOrder.getSellerUsername());
            tOrderInfo.setOrderId(tOrder.getOrderId());
            tOrderInfo.setTotal(tOrder.getTotal());
            tOrderInfo.setPhone(tOrder.getSellerPhone());
            tOrderInfo.setPrice(tOrder.getPrice());
            tOrderInfo.setUid(tOrder.getSellerUid());
            tOrderInfo.setTradeType(1);
        } else {
            tOrderInfo.setAddr(tOrder.getBuyerAddr());
            tOrderInfo.setAmount(tOrder.getAmount());
            tOrderInfo.setEndTime(tOrder.getEndTime());
            tOrderInfo.setUsername(tOrder.getBuyerUsername());
            tOrderInfo.setOrderId(tOrder.getOrderId());
            tOrderInfo.setTotal(tOrder.getTotal());
            tOrderInfo.setPhone(tOrder.getBuyerPhone());
            tOrderInfo.setPrice(tOrder.getPrice());
            tOrderInfo.setUid(tOrder.getBuyerUid());
            tOrderInfo.setTradeType(0);
        }
    }


    private void orderTrading(TOrder tOrder) {
        //卖家+u
        UserCurrencyStateReq sellerAdd = new UserCurrencyStateReq();
        setParam(tOrder.getSellerUid(), tOrder.getTotal(), sellerAdd, Long.valueOf(USDT.getCurrencyId()));
        tWalletInfoMapper.despoit(sellerAdd);
        //卖家-cat(冻结部分扣除)
        UserCurrencyStateReq sellerSub = new UserCurrencyStateReq();
        setParam(tOrder.getSellerUid(), tOrder.getAmount(), sellerSub, Long.valueOf(CAT.getCurrencyId()));
        tWalletInfoMapper.subFrozen(sellerSub);
        //买家-u
        UserCurrencyStateReq buyerSub = new UserCurrencyStateReq();
        setParam(tOrder.getBuyerUid(), tOrder.getTotal(), buyerSub, Long.valueOf(USDT.getCurrencyId()));
        int withdraw = tWalletInfoMapper.withdraw(buyerSub);
        if (withdraw == 0) {
            throw new BizException(BALANCE_NOT_ENOUGH);
        }
        //买家+cat
        UserCurrencyStateReq buyerAdd = new UserCurrencyStateReq();
        setParam(tOrder.getBuyerUid(), tOrder.getAmount(), buyerAdd, Long.valueOf(CAT.getCurrencyId()));
        tWalletInfoMapper.despoit(buyerAdd);
        //买家-cag
        UserCurrencyStateReq buyerSubCag = new UserCurrencyStateReq();
        setParam(tOrder.getBuyerUid(), tOrder.getCagFee(), buyerSubCag, Long.valueOf(CAG.getCurrencyId()));
        int buyCagSub = tWalletInfoMapper.withdraw(buyerSubCag);
        if (buyCagSub == 0) {
            throw new BizException(FEE_NOT_ENOUGH);
        }
        //卖家-cag（冻结部分扣除）
        UserCurrencyStateReq sellerSubCag = new UserCurrencyStateReq();
        setParam(tOrder.getSellerUid(), tOrder.getCagFee(), sellerSubCag, Long.valueOf(CAG.getCurrencyId()));
        int sellCagSub = tWalletInfoMapper.subFrozen(sellerSubCag);
        if (sellCagSub == 0) {
            throw new BizException(FEE_NOT_ENOUGH);
        }
    }

    private void checkOrderTimeOut(TOrder tOrder) {
        OrderOpenRes orderOpenRes = new OrderOpenRes();
        try {
            orderOpenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String orderTimeOut = orderOpenRes.getOrderTimeOut();
        long orderTimeOutL = Long.parseLong(orderTimeOut);
        long nowTime = System.currentTimeMillis();
        Long createTime = tOrder.getCreateTime();
        if (createTime + orderTimeOutL < nowTime) {
            throw new BizException(ORDER_TIME_OUT);
        }
    }


    private List addList(TOrder tOrder) {
        Date date = new Date();
        List<TWalletTradeOrder> list = new ArrayList<>();
        //卖家+u记录
        TWalletTradeOrder sellerOrder = new TWalletTradeOrder();
        sellerOrder.setCreateTime(date);
        sellerOrder.setDes(WalletTradeTypeEnum.SELL_CAT_INCOME.getTradeDesc());
        sellerOrder.setTradeType(WalletTradeTypeEnum.SELL_CAT_INCOME.getTradeType());
        sellerOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        sellerOrder.setCurrencyId(Long.valueOf(USDT.getCurrencyId()));
        sellerOrder.setCurrencyCode(USDT.getCurrencyCode());
        sellerOrder.setUid(tOrder.getSellerUid());
        sellerOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        sellerOrder.setTradeCurrencyNum(tOrder.getTotal());
        //写入买方信息
        sellerOrder.setTradeAddress(tOrder.getBuyerAddr());
        sellerOrder.setCounterPartyUid(tOrder.getBuyerUid());
        sellerOrder.setCounterPartyUserName(tOrder.getBuyerUsername());
        sellerOrder.setCounterPartyMobile(tOrder.getBuyerPhone());
        list.add(sellerOrder);
        //买家-u记录
        TWalletTradeOrder buyerUsdOrder = new TWalletTradeOrder();
        buyerUsdOrder.setCreateTime(date);
        buyerUsdOrder.setDes(WalletTradeTypeEnum.BUY_CAT_BUYER_SUBTRACT_USDT.getTradeDesc());
        buyerUsdOrder.setTradeType(WalletTradeTypeEnum.BUY_CAT_BUYER_SUBTRACT_USDT.getTradeType());
        buyerUsdOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        buyerUsdOrder.setCurrencyId(Long.valueOf(USDT.getCurrencyId()));
        buyerUsdOrder.setCurrencyCode(USDT.getCurrencyCode());
        buyerUsdOrder.setUid(tOrder.getBuyerUid());
        buyerUsdOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        buyerUsdOrder.setTradeCurrencyNum(tOrder.getTotal().abs().multiply(new BigDecimal(-1)));
        //写入卖方信息
        buyerUsdOrder.setTradeAddress(tOrder.getSellerAddr());
        buyerUsdOrder.setCounterPartyUid(tOrder.getSellerUid());
        buyerUsdOrder.setCounterPartyUserName(tOrder.getSellerUsername());
        buyerUsdOrder.setCounterPartyMobile(tOrder.getSellerPhone());
        list.add(buyerUsdOrder);
        //买家+cat记录
        TWalletTradeOrder buyerCatOrder = new TWalletTradeOrder();
        buyerCatOrder.setCreateTime(date);
        buyerCatOrder.setDes(WalletTradeTypeEnum.BUY_CAT_BUYER_ADD_CAT.getTradeDesc());
        buyerCatOrder.setTradeType(WalletTradeTypeEnum.BUY_CAT_BUYER_ADD_CAT.getTradeType());
        buyerCatOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        buyerCatOrder.setCurrencyId(Long.valueOf(CAT.getCurrencyId()));
        buyerCatOrder.setCurrencyCode(CAT.getCurrencyCode());
        buyerCatOrder.setUid(tOrder.getBuyerUid());
        buyerCatOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        buyerCatOrder.setTradeCurrencyNum(tOrder.getAmount());
        //写入卖方信息
        buyerCatOrder.setTradeAddress(tOrder.getSellerAddr());
        buyerCatOrder.setCounterPartyUid(tOrder.getSellerUid());
        buyerCatOrder.setCounterPartyUserName(tOrder.getSellerUsername());
        buyerCatOrder.setCounterPartyMobile(tOrder.getSellerPhone());
        list.add(buyerCatOrder);
        //买家-cag记录
        TWalletTradeOrder buyerCagOrder = new TWalletTradeOrder();
        buyerCagOrder.setCreateTime(date);
        buyerCagOrder.setDes(WalletTradeTypeEnum.BUY_CAT_CAG_FEE.getTradeDesc());
        buyerCagOrder.setTradeType(WalletTradeTypeEnum.BUY_CAT_CAG_FEE.getTradeType());
        buyerCagOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        buyerCagOrder.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
        buyerCagOrder.setCurrencyCode(CAG.getCurrencyCode());
        buyerCagOrder.setUid(tOrder.getBuyerUid());
        buyerCagOrder.setTradeSystemCode(WalletTradeSystemCodeEnum.APP_H5_SMART_CONTRACT.getCode());
        buyerCagOrder.setTradeCurrencyNum(tOrder.getCagFee().abs().multiply(new BigDecimal(-1)));
        list.add(buyerCagOrder);
        return list;
    }

    private void setParam(Long uid, BigDecimal amount, UserCurrencyStateReq sellerAdd, Long curId) {
        sellerAdd.setAmount(amount);
        sellerAdd.setUid(uid);
        sellerAdd.setCurrencyId(curId);//usdt
    }

    /**
     * 确认买入
     * 1.验证验证码
     *
     * @param sureBuyReq
     * @return
     */
    @Override
    public ResponseResult sureBuy(SureBuyReq sureBuyReq) {
        TOrder orderResult = tOrderService.selectOne(new EntityWrapper<TOrder>().eq("order_id", sureBuyReq.getOrderId()));
        if (!orderResult.getBuyerAddr().equals(sureBuyReq.getAddress())) {
            throw new BizException(ORDER_NO_PROMISSION);
        }
        if (orderResult.getState() == 1) {
            throw new BizException(ORDER_TIME_OUT);
        }
        if (orderResult.getState() == 2) {
            throw new BizException(ORDER_IS_TRADED);
        }
        this.checkOrderTimeOut(orderResult);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult checkPayPassword(CheckPwdReq checkPwdReq) {
        TUserInfo tUserInfo = new TUserInfo();
        tUserInfo.setUid(checkPwdReq.getUid());
        TUserInfo dbResult = tUserInfoMapper.selectOne(tUserInfo);
        String pwd = EncryptionUtil.md5Two(checkPwdReq.getPayPassword(), dbResult.getSalt());
        if (!pwd.equals(dbResult.getPayPassWord())) {
            throw new BizException(PAY_PASSWORD_ERROR);
        }
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult findOrderRecord(Long uid) {
        //卖单集合
        List<TOrder> sellList = tOrderService.selectList(new EntityWrapper<TOrder>().eq("seller_uid", uid));
        sellList.forEach(o -> {
            o.setType(2);
        });
        //买单集合
        List<TOrder> buyList = tOrderService.selectList(new EntityWrapper<TOrder>().eq("buyer_uid", uid));
        buyList.forEach(b -> {
            b.setType(1);
        });
        List<TOrder> list = new LinkedList();
        list.addAll(sellList);
        list.addAll(buyList);
        //创建时间倒序
        List<TOrder> collect = list.stream().sorted(Comparator.comparing(TOrder::getCreateTime).reversed()).collect(Collectors.toList());
        return ResponseResult.ok(collect);
    }

    @Override
    public ResponseResult checkSellCode(SellCatReq sellCatReq) {
        String phone = sellCatReq.getCountryCode()==86 ? sellCatReq.getPhone() : sellCatReq.getCountryCode()+sellCatReq.getPhone();
        log.info("countrycode:{},phone:{}",sellCatReq.getCountryCode(),sellCatReq.getPhone());
        //校验验证码
        String redisCode = "";
        if (!StringUtils.isEmpty(phone)) {
            redisCode = redisClient.get(SmsUtil.createPhoneKey(phone) + "_CAT_SELL");
            if (StringUtils.isEmpty(redisCode)) {
                throw new BizException("请发送验证码后再试");
            }
            log.info("用户 phoneRedisCode:{}", redisCode);
            log.info(sellCatReq.toString());
            if (redisCode == null || !redisCode.equals(sellCatReq.getCode())) {
                throw new BizException("验证码错误");
            }
            redisClient.deleteByKey(SmsUtil.createPhoneKey(phone) + "_CAT_SELL");
        } else {
            redisCode = redisClient.get(RedisConstant.EMAIL_CODE_KEY_PREFIX + sellCatReq.getEmail() + "_" + sellCatReq.getType());
            log.info("用户emailrediscode:{}", redisCode);
            log.info(sellCatReq.toString());
            if (redisCode == null || !redisCode.equals(sellCatReq.getCode())) {
                throw new BizException("验证码错误");
            }
            redisClient.deleteByKey(RedisConstant.EMAIL_CODE_KEY_PREFIX + sellCatReq.getEmail() + "_" + sellCatReq.getType());
        }
        redisClient.set("spotOK"+sellCatReq.getPhone(),true,5);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult checkCanTrade(SellCatReq sellCatReq) {
        //验证地址，节点是否可以转
        TOrder checkNodeParam = new TOrder();
        Long uid = this.getUidByCurrencyAddress(sellCatReq.getAddress());
        checkNodeParam.setBuyerUid(uid);
        checkNodeParam.setSellerUid(sellCatReq.getUid());
        this.checkNodePromissory(checkNodeParam);
        return ResponseResult.ok();
    }

    /**
     * 根据地址获取到uid,并且存入redis中，第一次是null，从db再取一次，再抛出异常
     * @param currencyAddress 交易地址
     * @return
     */
    private Long getUidByCurrencyAddress(@NotBlank String currencyAddress){
        String redisKey = RedisKeys.getUidByCurrencyAddress(RedisConstant.APP_REDIS_PREFIX , currencyAddress);
        String uidChar = redisClient.get(redisKey);
        Long uid = null;
        if(null==uidChar){ //if new currency address
            uid = tWalletInfoMapper.queryUidbyAddr(currencyAddress);
            //可能是null
            if(null==uid) {
                throw new BizException("您设定的买家地址不是本钱包内部地址");
            }
            //存双方向
            redisClient.setDay(redisKey,uid.toString(),10);
            String getAddressByUidRedisKey = RedisKeys.getCurrencyAddressByUid(RedisConstant.APP_REDIS_PREFIX,uid);
            redisClient.setDay(getAddressByUidRedisKey,currencyAddress,10);
        }else{
            uid = Long.parseLong(uidChar);
        }
        return uid;
    }
}
