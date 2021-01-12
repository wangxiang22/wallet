package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.enums.*;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.malechain.enums.WithdrawStatusEnum;
import com.xinlian.member.biz.malechain.vo.request.MaleChainRechargeCallBackRequest;
import com.xinlian.member.biz.malechain.vo.request.MaleChainWithdrawCallBackRequest;
import com.xinlian.member.biz.service.PushNoticeService;
import com.xinlian.member.biz.service.TWalletInfoService;
import com.xinlian.member.biz.service.TWalletTradeOrderService;
import com.xinlian.member.biz.udun.UdunConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 钱包交易订单表 服务实现类
 * </p>
 *
 * @since 2019-12-23
 */
@Service
@Slf4j
public class TWalletTradeOrderServiceImpl extends ServiceImpl<TWalletTradeOrderMapper, TWalletTradeOrder> implements TWalletTradeOrderService {

    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TWalletInfoService walletInfoService;
    @Autowired
    private PushNoticeService pushNoticeService;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;

    @Override
    public Integer saveWalletTradeOrder(TWalletTradeOrder walletTradeOrder) {
        return walletTradeOrderMapper.saveModel(walletTradeOrder);
    }

    @Override
    public Integer updateWalletTradeOrder(TWalletTradeOrder walletTradeOrder) {
        return walletTradeOrderMapper.updateWalletTradeOrder(walletTradeOrder);
    }

    public TWalletTradeOrder getTWalletTradeOrder(Long businessId, int newTradeStatus, int oldTradeStatus,BigDecimal minersFee,String txId){
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setId(businessId);
        walletTradeOrder.setTradeStatus(newTradeStatus);
        walletTradeOrder.setOldTradeStatus(oldTradeStatus);
        walletTradeOrder.setMinersFee(minersFee);
        walletTradeOrder.setTxId(txId);
        return walletTradeOrder;
    }

    public Integer updateModelByTradeStatus(Long businessId, int newTradeStatus, int oldTradeStatus,BigDecimal minersFee,String txId) {
        TWalletTradeOrder walletTradeOrder = this.getTWalletTradeOrder(businessId,newTradeStatus,oldTradeStatus,minersFee,txId);
        return walletTradeOrderMapper.updateWalletTradeOrder(walletTradeOrder);
    }

    @Override
    public Integer auditPass(long businessId) {
        return this.updateModelByTradeStatus(businessId, WalletTradeOrderStatusEnum.AUDIT_PASS.getCode(),WalletTradeOrderStatusEnum.WAITING_CALLBACK.getCode(),null,"");
    }

    @Override
    public Integer tradeRefused(long businessId) {
        return this.updateModelByTradeStatus(businessId,WalletTradeOrderStatusEnum.AUDIT_REJECT.getCode(),WalletTradeOrderStatusEnum.WAITING_CALLBACK.getCode(),null,"");
    }

    @Override
    public Integer tradeSuccess(long businessId, BigDecimal minersFee, String txId) {
        //更改交易单状态
        int resultNum = this.updateModelByTradeStatus(businessId,WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode(),WalletTradeOrderStatusEnum.AUDIT_PASS.getCode(),
                minersFee,txId);
        return resultNum;
    }



    @Override
    public Integer tradeFail(long businessId, String failReason) {
        TWalletTradeOrder walletTradeOrder = this.getTWalletTradeOrder(businessId,WalletTradeOrderStatusEnum.TRADE_FAIL.getCode(),WalletTradeOrderStatusEnum.AUDIT_PASS.getCode(),null,"");
        walletTradeOrder.setFailReason(failReason);
        return this.updateWalletTradeOrder(walletTradeOrder);
    }
    @Override
    public Integer tradeDoRequestFail(long businessId,String failReason) {
        TWalletTradeOrder walletTradeOrder = this.getTWalletTradeOrder(businessId,WalletTradeOrderStatusEnum.TRADE_FAIL.getCode(),WalletTradeOrderStatusEnum.ADMIN_PASS_PASS.getCode(),null,"");
        walletTradeOrder.setFailReason(failReason);
        return this.updateWalletTradeOrder(walletTradeOrder);
    }

    @Override
    public void transactionalUnFreezeBiz(TWalletTradeOrder walletTradeOrder){
        //1.交易订单设置失败
        int tradeResultNum = this.tradeDoRequestFail(walletTradeOrder.getId(),"请求优盾提币接口报错！");
        //设置交易订单失败-业务成功后进行，2.钱包解冻
        //待解冻金额转化为正整数
        BigDecimal plusFrozenNum = UdunBigDecimalUtil.convertPlus(walletTradeOrder.getTradeCurrencyNum());
        int resultNum = walletInfoService.disposalBalanceAndUnFreeze(walletTradeOrder.getUid(), walletTradeOrder.getCurrencyId(), plusFrozenNum);
        if(resultNum==0||tradeResultNum==0){
            log.error("处理解冻金额失败，请核对!uid:{},tradeOrderId:{}", walletTradeOrder.getUid(), walletTradeOrder.getId());
            throw new BizException("提币审核通过请求优盾提币后解冻异常！");
        }
    }

    @Override
    @Transactional
    public String chargeMoneyCallbackHandle(MaleChainRechargeCallBackRequest maleChainRechargeCallBackRequest,String rechargeOperType) {
        //验证是否重复推送过充值，推个充值，也返回OK
        if(this.checkChargeMoneyTradeRecord(maleChainRechargeCallBackRequest.getAddress(),maleChainRechargeCallBackRequest.getTx_hash())){
            return UdunConstant.MAIL_CHAIN_OK;
        }
        BigDecimal amount = UdunBigDecimalUtil.defaultDisposeValueDecimal(maleChainRechargeCallBackRequest.getTradeNumber()+"");
        //检验推送的数值大小
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("地址：{}充币（币种：{},主币种：{}）数量({})小于等于0，系统不允许录入数据"
                    ,maleChainRechargeCallBackRequest.getAddress(),"","",amount.toPlainString());
            throw new BizException(ErrorInfoEnum.DEPOSIT_AMOUNT_ERROR);
        }
        //检测u盾数据 跟之前数据
        TWalletInfo getWalletInfo = this.walletInfoByChargeMoneyAddress(maleChainRechargeCallBackRequest.getAddress(),rechargeOperType);
        if(null==getWalletInfo){throw new BizException(ErrorInfoEnum.CALL_BACK_WALLET_INFO_ERROR);}
        //1.写入交易记录表
        int tradeResultNum = this.buildWalletTradeOrderBasicModel(maleChainRechargeCallBackRequest,amount,getWalletInfo);
        //2.计入客户钱包表
        getWalletInfo.setBalanceNum(UdunBigDecimalUtil.addNum(amount,getWalletInfo.getBalanceNum()));
        //更新钱包数值
        int wallInfoResultNum = walletInfoMapper.updateModel(getWalletInfo);
        boolean tradeResultFlag = true;
        if(tradeResultNum==0||wallInfoResultNum==0){
            tradeResultFlag = false;
            throw new BizException(ErrorInfoEnum.DEPOSIT_AMOUNT_INSERT_ERROR);
        }
        //进行极光推送
        pushJiGuangNotice(getWalletInfo,tradeResultFlag,amount);
        //账户大额变动流水记录
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(getWalletInfo.getUid(),getWalletInfo.getCurrencyId(),getWalletInfo.getCurrencyCode(),null,
                maleChainRechargeCallBackRequest.getAddress(),amount,WalletTradeTypeEnum.TOP_UP.getTradeDesc(),maleChainRechargeCallBackRequest.getTx_hash(),new Date(),
                UniqueNoUtil.uuid());
        return UdunConstant.MAIL_CHAIN_OK;
    }

    /**
     * 提币回调状态逻辑判断处理
     * @param maleChainWithdrawCallBackRequest
     */
    @Override
    @Transactional
    public void withdrawCashCallbackDispose(MaleChainWithdrawCallBackRequest maleChainWithdrawCallBackRequest){
        long businessId = Long.parseLong(maleChainWithdrawCallBackRequest.getBusinessId());
        //获取交易单
        TWalletTradeOrder tradeOrder = this.getWalletTradeOrderById(businessId);
        BigDecimal tradeCurrencyNumPlus = UdunBigDecimalUtil.convertPlus(tradeOrder.getTradeCurrencyNum());
        if(null==tradeOrder){throw new BizException("不存在提币订单!["+businessId+"]");}
        if (maleChainWithdrawCallBackRequest.getStatus().intValue() == WithdrawStatusEnum.SUCCESS.getCode().intValue()) { // 交易完成
            // 交易完成回调处理，处理本地记录的状态，并将提币的资金信息做变更 --（减除冻结金额）
            TWalletInfo getWalletInfo = this.getWalletInfo(tradeOrder.getUid());
            getWalletInfo.setFrozenNum(UdunBigDecimalUtil.subtractNum(getWalletInfo.getFrozenNum(),tradeCurrencyNumPlus));
            int resultNumByWalletInfo = walletInfoService.updateModel(getWalletInfo);
            BigDecimal minersFee = UdunBigDecimalUtil.disposeValueDecimal(maleChainWithdrawCallBackRequest.getFee(),UdunConstant.MINERS_DECIMALS);
            //update更新订单-成功
            int resultNum = this.tradeSuccess(Long.parseLong(maleChainWithdrawCallBackRequest.getBusinessId()),minersFee,maleChainWithdrawCallBackRequest.getTx_hash());
            if(resultNum==0 || resultNumByWalletInfo == 0){
                throw new BizException("提币回调写数据异常!");
            }
            //记录推送记录
            pushNoticeService.saveAppNoticePushRecord(getWalletInfo.getUid(),null,getWalletInfo.getCurrencyCode(),tradeCurrencyNumPlus.stripTrailingZeros().toPlainString(),JPushTitleMessageEnum.CASH_SUCCESS);
            //账户大额变动流水记录
            withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(tradeOrder.getUid(),tradeOrder.getCurrencyId(),tradeOrder.getCurrencyCode(),null,
                    tradeOrder.getTradeAddress(),tradeOrder.getTradeCurrencyNum(),tradeOrder.getDes(),tradeOrder.getTxId(),new Date(),UniqueNoUtil.uuid());
        }else if (maleChainWithdrawCallBackRequest.getStatus().intValue() == WithdrawStatusEnum.FAILURE.getCode().intValue()) { // 交易失败
            TWalletInfo getWalletInfo = this.getWalletInfo(tradeOrder.getUid());
            getWalletInfo.setFrozenNum(UdunBigDecimalUtil.subtractNum(getWalletInfo.getFrozenNum(),tradeCurrencyNumPlus));
            getWalletInfo.setBalanceNum(UdunBigDecimalUtil.addNum(getWalletInfo.getBalanceNum(),tradeCurrencyNumPlus));
            int resultNumByWalletInfo = walletInfoService.updateModel(getWalletInfo);
            // 交易失败回调处理，处理本地记录的状态，并解冻资金
            int resultNum = this.tradeFail(Long.parseLong(maleChainWithdrawCallBackRequest.getBusinessId()),"交易失败");
            if(resultNum==0 || resultNumByWalletInfo == 0){
                throw new BizException("提币回调写数据异常!");
            }
            //记录推送记录
            pushNoticeService.saveAppNoticePushRecord(getWalletInfo.getUid(),null,getWalletInfo.getCurrencyCode(),tradeCurrencyNumPlus.stripTrailingZeros().toPlainString(),JPushTitleMessageEnum.CASH_FAIL);
        }else{
            log.error("提币回调状态[{}],找不到对应的操作",maleChainWithdrawCallBackRequest.getStatus());
            throw new BizException("找不到对应的交易状态status:"+maleChainWithdrawCallBackRequest.getStatus());
        }
    }

    /**
     * 提币回调 获取默认充币钱包地址对象 - uid
     * @param uid
     * @return
     */
    public TWalletInfo getWalletInfo(Long uid){
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setUid(uid);
        walletInfo.setCurrencyId(Long.parseLong(CurrencyEnum.USDT.getCurrencyId()+""));
        return walletInfoService.getByCriteria(walletInfo);
    }

    @Override
    public void pushJiGuangNotice(TWalletInfo  getWalletInfo,boolean tradeResultFlag,BigDecimal amount){
        if (tradeResultFlag) {//充币成功
            pushNoticeService.saveAppNoticePushRecord(getWalletInfo.getUid(),null,getWalletInfo.getCurrencyCode(),amount.stripTrailingZeros().toPlainString(),JPushTitleMessageEnum.RECHARGE_SUCCESS);
        }else{//充币失败
            pushNoticeService.saveAppNoticePushRecord(getWalletInfo.getUid(),null,getWalletInfo.getCurrencyCode(),amount.stripTrailingZeros().toPlainString(),JPushTitleMessageEnum.RECHARGE_FAIL);
        }
    }


    @Override
    public TWalletTradeOrder getWalletTradeOrderById(long businessId) {
        TWalletTradeOrder whereTradeOrder = new TWalletTradeOrder();
        whereTradeOrder.setId(businessId);
        return walletTradeOrderMapper.getByCriteria(whereTradeOrder);
    }

    @Override
    public TWalletTradeOrder getAdminAuditPassTradeOrder() {
        return walletTradeOrderMapper.getAdminAuditPassTradeOrder();
    }

    /**
     * 充币回调 获取默认充币钱包地址对象
     * @param address
     * @param rechargeOperType
     * @return
     */
    private TWalletInfo walletInfoByChargeMoneyAddress(String address,String rechargeOperType){
        TWalletInfo whereTWalletInfo = new TWalletInfo();
        if(RechargeOperTypeEnum.UDUN_RECHARGE.getOperType().equals(rechargeOperType)){
            whereTWalletInfo.setUdunCurrencyAddress(address);
        }else {
            whereTWalletInfo.setCurrencyAddress(address);
        }
        whereTWalletInfo.setCurrencyCode(CurrencyEnum.USDT.getCurrencyCode());
        TWalletInfo walletInfo = walletInfoMapper.getByCriteria(whereTWalletInfo);
        return walletInfo;
    }

    private Integer buildWalletTradeOrderBasicModel(MaleChainRechargeCallBackRequest maleChainRechargeCallBackRequest,BigDecimal amount,TWalletInfo walletInfo){
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        walletTradeOrder.setTxId(maleChainRechargeCallBackRequest.getTx_hash());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.TOP_UP.getTradeType());
        walletTradeOrder.setDes(WalletTradeTypeEnum.TOP_UP.getTradeDesc());
        walletTradeOrder.setCurrencyId(walletInfo.getCurrencyId());
        walletTradeOrder.setCurrencyCode(walletInfo.getCurrencyCode());
        walletTradeOrder.setTradeAddress(maleChainRechargeCallBackRequest.getAddress());
        walletTradeOrder.setTradeCurrencyNum(amount);
        //矿工费收取的是主币eth  eth的token就用固定精度18取处理
        walletTradeOrder.setMinersFee(UdunBigDecimalUtil.disposeValueDecimal(maleChainRechargeCallBackRequest.getFee(), UdunConstant.MINERS_DECIMALS));
        //根据回调交易地址找到
        walletTradeOrder.setUid(walletInfo.getUid());
        walletTradeOrder.setCreateTime(new Date());
        return walletTradeOrderMapper.saveModel(walletTradeOrder);
    }


    private boolean checkChargeMoneyTradeRecord(String address,String txId){
        TWalletTradeOrder whereModel = new TWalletTradeOrder();
        whereModel.setTxId(txId);
        whereModel.setTradeAddress(address);
        TWalletTradeOrder walletTradeOrder = walletTradeOrderMapper.getByCriteria(whereModel);
        if (null!=walletTradeOrder){
            log.info("重复[{}]推送 txid:[{}]","充币回调",txId);
            //throw new BizException(ErrorInfoEnum.EXIST_CHARGE_MONEY_TRADE);
            return true;
        }
        return false;
    }



}
