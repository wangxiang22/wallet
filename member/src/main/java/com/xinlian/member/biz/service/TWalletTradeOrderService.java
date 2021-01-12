package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.member.biz.malechain.vo.request.MaleChainRechargeCallBackRequest;
import com.xinlian.member.biz.malechain.vo.request.MaleChainWithdrawCallBackRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 * <p>
 * 钱包交易订单表 服务类
 * </p>
 *
 * @since 2019-12-23
 */
public interface TWalletTradeOrderService extends IService<TWalletTradeOrder> {

    /**
     * 新增
     * @param walletTradeOrder
     * @return
     */
    Integer saveWalletTradeOrder(TWalletTradeOrder walletTradeOrder);

    /**
     * 更新
     * @param walletTradeOrder
     * @return
     */
    Integer updateWalletTradeOrder(TWalletTradeOrder walletTradeOrder);

    /**
     * 提币 - 审核通过
     * @param businessId
     */
    Integer auditPass(long businessId);

    /**
     * 提币 - 审核拒绝
     * @param businessId
     */
    Integer tradeRefused(long businessId);

    /**
     * 提币 - 支付成功
     * @param businessId
     * @param minersFee
     * @param txId
     */
    Integer tradeSuccess(long businessId, BigDecimal minersFee, String txId);

    /**
     * 提币 - 交易失败
     * @param businessId
     */
    Integer tradeFail(long businessId,String failReason);

    /**
     * 请求提币 - 直接返回失败时候调用
     * @param businessId
     * @param failReason
     * @return
     */
    Integer tradeDoRequestFail(long businessId,String failReason);

    /**
     * 请求交易失败 根据交易单-
     * 解冻交易金额
     * @param walletTradeOrder
     */
    @Transactional
    void transactionalUnFreezeBiz(TWalletTradeOrder walletTradeOrder);

    /**
     * 充币 - 回调
     * @param maleChainRechargeCallBackRequest
     * @param rechargeOperType 充值类别-来源
     */
    String chargeMoneyCallbackHandle(MaleChainRechargeCallBackRequest maleChainRechargeCallBackRequest,String rechargeOperType);

    /**
     * 提币 - 回调
     */
    void withdrawCashCallbackDispose(MaleChainWithdrawCallBackRequest maleChainWithdrawCallBackRequest);

    /**
     * 获取后台审核通过的 提币交易订单 一条记录
     * @return
     */
    TWalletTradeOrder getAdminAuditPassTradeOrder();

    /**
     * 根据主键获取
     * @param businessId
     * @return
     */
    TWalletTradeOrder getWalletTradeOrderById(long businessId);

    void pushJiGuangNotice(TWalletInfo getWalletInfo, boolean tradeResultFlag, BigDecimal amount);
}
