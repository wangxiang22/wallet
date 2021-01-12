package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.*;
import com.xinlian.common.request.PledgeReq;
import com.xinlian.common.response.PledgeAmountCurrencyRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.PledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 矿池质押 服务实现类
 * @author lt
 * @since 2020-06-04
 */
@Slf4j
@Service
public class PledgeServiceImpl implements PledgeService {

    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;

    @Override
    public ResponseResult findPledgeAmountCurrency(Long userId,Long nodeId) {
        if (!allowPledge(userId,nodeId)){
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg("此功能暂未开放！").build();
        }
        PledgeAmountCurrencyRes pledgeAmountCurrencyRes = new PledgeAmountCurrencyRes();
        //质押币种定为CAT
        pledgeAmountCurrencyRes.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
        pledgeAmountCurrencyRes.setPledgeAmount(getPledgeAmount());
        return ResponseResult.ok(pledgeAmountCurrencyRes);
    }

    @Override
    @Transactional
    public ResponseResult submitPledgeApply(PledgeReq pledgeReq) {
        log.info("uid:{}，发起质押操作，请求参数：{}", pledgeReq.getUid(), JSON.toJSONString(pledgeReq));
        if (!allowPledge(pledgeReq.getUid(),pledgeReq.getNodeId())){
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg("此功能暂未开放！").build();
        }
        if (UdunBigDecimalUtil.judgeIfMinus(pledgeReq.getPledgeAmount().toString())) {
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg("保证金金额有误！").build();
        }
        //判断用户是否有正在审核中或是已审核通过的质押流水记录
        if (findPledgeTradeOrder(pledgeReq.getUid())) {
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg("您的申请已提交或已通过，请勿重复提交！").build();
        }
        //先判断支付密码是否正确
        TUserInfo tUserInfo = userInfoMapper.selectById(pledgeReq.getUid());
        if (null == tUserInfo) {
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg("网络拥堵，请稍后再试！").build();
        }
        String md5PassWord = EncryptionUtil.md5Two(pledgeReq.getPayPassWord(), tUserInfo.getSalt());
        if (!tUserInfo.getPayPassWord().equals(md5PassWord)) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg(CommonEnum.PAY_PASSWORD_NOT_MATCH.getDes()).build();
        }
        //钱包余额扣除质押金，同时添加到冻结金额字段中，sql语句有判断余额是否充足
        TWalletInfo tWalletInfo = new TWalletInfo();
        tWalletInfo.setUid(pledgeReq.getUid());
        tWalletInfo.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tWalletInfo.setFrozenNum(getPledgeAmount());
        int freezeResult = walletInfoMapper.disposalBalanceAndFreeze(tWalletInfo);
        if (0 == freezeResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg(CommonEnum.BALANCE_NOT_ENOUGH.getDes() + "，请充值！").build();
        }
        //钱包流水表增加扣款记录
        TWalletTradeOrder tWalletTradeOrder = createTWalletTradeOrder(pledgeReq.getUid());
        Integer insertResult = walletTradeOrderMapper.insert(tWalletTradeOrder);
        if (0 == insertResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg(CommonEnum.PLEDGE_FAIL.getDes()).build();
        }
        TUserInfo userInfo = new TUserInfo();
        userInfo.setUid(pledgeReq.getUid());
        //2：待审核质押
        userInfo.setPledgeState(2);
        Integer updateResult = userInfoMapper.updateById(userInfo);
        if (0 == updateResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(ErrorInfoEnum.FAILED.getCode()).msg(CommonEnum.PLEDGE_FAIL.getDes()).build();
        }
        //账户大额变动流水记录
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(pledgeReq.getUid(),Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())),CurrencyEnum.CAT.getCurrencyCode(),
                null,null,UdunBigDecimalUtil.convertMinus(getPledgeAmount()),WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc(),null,
                new Date(), UniqueNoUtil.uuid());
        return ResponseResult.builder().code(ErrorInfoEnum.SUCCESS.getCode()).msg(CommonEnum.PAY_SUCCESS_WAIT_AUDIT.getDes()).build();
    }

    /**
     * 查询用户的质押流水记录
     * @param uid 用户uid
     * @return 查询结果
     */
    private boolean findPledgeTradeOrder(Long uid) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(uid);
        tWalletTradeOrder.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tWalletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        tWalletTradeOrder.setDes(WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc());
        TWalletTradeOrder walletTradeOrder = walletTradeOrderMapper.selectOne(tWalletTradeOrder);
        if (null != walletTradeOrder) {
            return true;
        }
        TWalletTradeOrder tradeOrder = new TWalletTradeOrder();
        tradeOrder.setUid(uid);
        tradeOrder.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        tradeOrder.setDes(WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc());
        TWalletTradeOrder order = walletTradeOrderMapper.selectOne(tradeOrder);
        return null != order;
    }

    /**
     * 配置项中获取保证金交纳金额
     * @return 保证金交纳金额
     */
    private BigDecimal getPledgeAmount() {
        String pledgeAmount = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_PLEDGE.getBelongsSystemCode());
        if(null != pledgeAmount && !"".equals(pledgeAmount)) {
            return new BigDecimal(pledgeAmount).setScale(4,BigDecimal.ROUND_DOWN);
        }else {
            throw new BizException("获取保证金金额异常，请稍后重试！");
        }
    }

    /**
     * 创建保证金扣款记录实体
     * @param uid 用户uid
     * @return 保证金扣款记录实体
     */
    private TWalletTradeOrder createTWalletTradeOrder(Long uid) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(uid);
        tWalletTradeOrder.setCurrencyId(Long.parseLong(String.valueOf(CurrencyEnum.CAT.getCurrencyId())));
        tWalletTradeOrder.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
        tWalletTradeOrder.setTradeCurrencyNum(UdunBigDecimalUtil.convertMinus(getPledgeAmount()));
        tWalletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        tWalletTradeOrder.setTradeType(WalletTradeTypeEnum.PLEDGE_MINING.getTradeType());
        tWalletTradeOrder.setDes(WalletTradeTypeEnum.PLEDGE_MINING.getTradeDesc());
        tWalletTradeOrder.setCreateTime(new Date());
        return tWalletTradeOrder;
    }

    /**
     * 判断申请质押的用户是否符合条件
     * @param userId 用户uid
     * @param nodeId 用户节点id
     * @return true：允许质押，false：不允许质押
     */
    private boolean allowPledge(Long userId,Long nodeId) {
        //多一层判断，用户是否已激活
        TUserInfo userInfo = userInfoMapper.selectById(userId);
        if (0 == userInfo.getOremState()) {
            return false;
        }
        //先判断配置项中的开关
        TServerNode node = serverNodeService.getById(nodeId);
        if (0 == node.getPledgeStatus()) {
            return false;
        }
        return true;
    }
}
