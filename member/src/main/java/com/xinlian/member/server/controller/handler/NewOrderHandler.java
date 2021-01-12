package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TNewOrderMapper;
import com.xinlian.biz.dao.TUserAuthMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.model.*;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.UserLevelStatusEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.request.OrderReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.PrStringUtils;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.TWalletTradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.xinlian.common.contants.OrderConstant.PAY_PASSWORD_ERROR;
import static com.xinlian.common.enums.WalletTradeOrderStatusEnum.TRADE_SUCCESS;

@Component
@Slf4j
public class NewOrderHandler {
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TUserInfoMapper tUserInfoMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private TWalletTradeOrderService tWalletTradeOrderService;
    @Autowired
    private TNewOrderMapper tNewOrderMapper;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private TUserAuthMapper tUserAuthMapper;

    /**
     * 检查必填参数
     */
    public void checkNullParam(OrderReq orderReq){
        if(orderReq.getAmount() == null || orderReq.getAmount() <= 0 || orderReq.getAmount() > 9999){
            throw new BizException("请填写合法的购买数量");
        }
        if(StringUtils.isBlank(orderReq.getGoodsName())){
            throw new BizException("请选择商品");
        }
        if(StringUtils.isBlank(orderReq.getAddress())){
            throw new BizException("请填写收货地址");
        }
        if(StringUtils.isBlank(orderReq.getPhone())){
            throw new BizException("请填写收件人手机号");
        }
        if(StringUtils.isBlank(orderReq.getUserName())){
            throw new BizException("请填写收件人");
        }
        if(StringUtils.isBlank(orderReq.getPassword())){
            throw new BizException("请输入支付密码");
        }
        if(StringUtils.isBlank(orderReq.getCode())){
            throw new BizException("请输入短信验证码");
        }
        if(StringUtils.isBlank(orderReq.getChainName())){
            throw new BizException("请选择链区");
        }
    }

    /**
     * 校验用户的合法性
     * @param userInfo
     */
    public String checkUserStatus(TUserInfo userInfo, OrderReq orderReq) {
        //校验用户信息
        if(userInfo == null){
            throw new BizException("用户信息有误");
        }
        if(StringUtils.isBlank(userInfo.getMobile())){
            throw new BizException("请先绑定手机号");
        }
        //校验用户状态
        if(UserLevelStatusEnum.FREEZE.getCode() == userInfo.getLevelStatus()){
            throw new BizException("对方账户异常，请核实!");
        }
        //校验支付密码
        String password = EncryptionUtil.md5Two(orderReq.getPassword(), userInfo.getSalt());
        if (!password.equals(userInfo.getPayPassWord())) {
            throw new BizException(PAY_PASSWORD_ERROR);
        }
        //校验验证码
        String phone = userInfo.getCountryCode()==86 ? userInfo.getMobile()
                : userInfo.getCountryCode().toString().concat(userInfo.getMobile());
        return this.doCheckCode(orderReq, phone);
    }

    private String doCheckCode(OrderReq orderReq, String phone) {
        String redisKey = SmsUtil.createPhoneKey(phone).concat("_BUY_GOODS");
        if (!StringUtils.isEmpty(phone)) {
            String redisCode = redisClient.get(redisKey);
            if (StringUtils.isEmpty(redisCode)) {
                throw new BizException("请发送验证码后再试");
            }
            if (!redisCode.equals(orderReq.getCode())) {
                checkSmsRuleHandler.doSaveSmsRuleHandler(phone, "BUY_GOODS");
                throw new BizException("验证码错误");
            }
            checkSmsRuleHandler.doDeleteSmsRuleHandler(phone, "BUY_GOODS");
            return redisKey;
        } else {
            throw new BizException("请发送验证码后再试");
        }
    }

    /**
     * 创建订单
     * @param orderReq
     * @param fromUser
     * @return
     */
    @Transactional
    public boolean crateOrder(OrderReq orderReq, TUserInfo fromUser) {
        //获得接收方uid(写死) 1936779
        TUserInfo toUser = tUserInfoMapper.selectById(1936779L);
        //接收方手机号展示时中间四位数*号
        toUser.setMobile(PrStringUtils.mobileTuoMin(toUser.getMobile()));
        //读取系统配置(商品单价)
        String usdtAmountStr = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum
                .BUY_GOODS_MASK_MONEY_USDT.getBelongsSystemCode());
        if(StringUtils.isBlank(usdtAmountStr) || usdtAmountStr.contains("-")){
            throw new BizException("读取系统配置出错");
        }
        //获得需要的usdt(总价)
        BigDecimal usdtAmount = new BigDecimal(usdtAmountStr).multiply(new BigDecimal(orderReq.getAmount()));
        //买方扣減usdt
        TWalletInfo fromWalletInfo = tWalletInfoMapper.queryWalletByUid(fromUser.getUid(), "USDT");
        fromWalletInfo.setMovableAssetsNum(usdtAmount);
        int reduceCount = tWalletInfoMapper.fromWalletInfoAbatmentBalanceNum(fromWalletInfo);
        if(reduceCount == 0){
            throw new BizException("余额不足");
        }
        //卖方增加usdt
        TWalletInfo toWalletInfo = tWalletInfoMapper.queryWalletByUid(toUser.getUid(), "USDT");
        toWalletInfo.setMovableAssetsNum(usdtAmount);
        int addCount = tWalletInfoMapper.toWalletInfoAddBalanceNum(toWalletInfo);
        if(addCount == 0){
            throw new BizException("下单失败");
        }
        //买方生成流水
        this.createWalletTradeOrder(toWalletInfo, fromUser, toUser, usdtAmount.multiply(new BigDecimal(-1)), "购买商品");
        //卖方生成流水
        this.createWalletTradeOrder(fromWalletInfo, toUser, fromUser, usdtAmount, "出售商品");
        //生成订单
        this.createOrderEntity(orderReq, fromUser, toUser, usdtAmount, new BigDecimal(usdtAmountStr));

        //账户大额变动流水记录
        String uuid = UniqueNoUtil.uuid();
        //卖方增加usdt（uid(写死) 1936779）
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(toUser.getUid(), Long.parseLong(CurrencyEnum.USDT.getCurrencyId() + ""), CurrencyEnum.USDT.getCurrencyCode(),
                fromUser.getUid(),fromWalletInfo.getCurrencyAddress(),usdtAmount,"出售商品",null,new Date(),uuid);
        //买方扣減usdt
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(fromUser.getUid(), Long.parseLong(CurrencyEnum.USDT.getCurrencyId() + ""), CurrencyEnum.USDT.getCurrencyCode(),
                toUser.getUid(),toWalletInfo.getCurrencyAddress(), usdtAmount.multiply(new BigDecimal("-1")),"购买商品",null,new Date(),uuid);
        return true;
    }

    private void createOrderEntity(OrderReq orderReq, TUserInfo fromUser, TUserInfo toUser, BigDecimal usdtAmount,
                                   BigDecimal price) {
        TNewOrder tNewOrder = new TNewOrder();
        BeanUtils.copyProperties(orderReq, tNewOrder);
        tNewOrder.setUid(fromUser.getUid());
        tNewOrder.setCreateTime(new Date());
        tNewOrder.setPrice(price);
        tNewOrder.setOrderNo(CommonUtil.getRandomString(16));
        int resultNum =  tNewOrderMapper.insert(tNewOrder);
        if(resultNum == 0 ){
            log.error("下单出现异常：{}", JSONObject.toJSONString(tNewOrder));
            throw new BizException("下单出现异常!");
        }
    }

    private void createWalletTradeOrder(TWalletInfo toWalletInfo, TUserInfo fromUser,
                                                     TUserInfo toUser, BigDecimal usdtAmount, String des) {
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setCurrencyId(5L);
        walletTradeOrder.setCurrencyCode("USDT");
        walletTradeOrder.setTradeAddress(toWalletInfo.getCurrencyAddress());
        walletTradeOrder.setTradeStatus(TRADE_SUCCESS.getCode());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.getEnumTradeType(des));
        walletTradeOrder.setDes(des);
        walletTradeOrder.setCounterPartyMobile(toUser.getMobile());
        walletTradeOrder.setCounterPartyUserName(toUser.getUserName());
        walletTradeOrder.setCounterPartyUid(toUser.getUid());
        walletTradeOrder.setUid(fromUser.getUid());
        walletTradeOrder.setTradeCurrencyNum(usdtAmount);
        int resultNum =  tWalletTradeOrderService.saveWalletTradeOrder(walletTradeOrder);
        if(resultNum == 0 ){
            log.error("保存购物流水时出现异常：{}", JSONObject.toJSONString(walletTradeOrder));
            throw new BizException("出现系统异常!");
        }
    }


    public TUserAuth checkNodeAndAmount(TUserInfo userInfo, OrderReq orderReq) {
        //-----校验节点是否允许购买
        TServerNode serverNode = serverNodeService.getById(userInfo.getServerNodeId());
        if(serverNode == null){
            throw new BizException("获取当前用户节点出现异常");
        }
        String pids = serverNode.getParentIds();
        if(StringUtils.isBlank(pids)){
            throw new BizException("获取用户节点出现异常");
        }
        if(StringUtils.equals("0", pids)){//当前节点为一级节点
            //直接判断是否为新大陆
            if(serverNode.getId().longValue() == 7L){
                throw new BizException("当前节点暂未开放购买");
            }
        }else{
            //获取所有父节点列表
            String[] pidsArr = pids.split(",");
            if(pids.length() == 1){
                throw new BizException("节点结构出现异常");
            }
            //得到一级节点id
            Long pid = Long.parseLong(pidsArr[1]);
            //判断是否为新大陆
            if(pid.longValue() == 7L){
                throw new BizException("当前节点暂未开放购买");
            }
        }

        //-----校验当前购买总数是否超过限制
        //获取当前用户的实名信息
        TUserAuth tUserAuth = new TUserAuth();
        tUserAuth.setUid(userInfo.getUid());
        tUserAuth.setStatus(3);
        TUserAuth auth = tUserAuthMapper.selectOne(tUserAuth);
        if(auth == null){
            throw new BizException("未获得当前用户的实名信息");
        }
        //获取当前购买总数
        Integer count = redisClient.get("AUTH_BUY_TOTAL".concat(auth.getAuthSn()));
        if(count == null){
            count = 0;
        }
        log.info(DateFormatUtil.getByNowTime(7) + " 单个用户最多可购买16盒，已购买{}盒,新订单{}盒",count,orderReq.getAmount());
        if((count + orderReq.getAmount()) > 16L){
            throw new BizException("单个用户最多可购买16盒");
        }
        return auth;
    }
}
