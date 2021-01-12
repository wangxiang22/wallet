package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lt
 * @date 2020/08/24
 **/
@Data
public class EveryDayBillDetailRes {
    /**
     * 导出的数据日期
     */
    private String time;
    /**
     * 内部充币
     */
    private BigDecimal usdtInternalRecharge;
    private BigDecimal catInternalRecharge;
    private BigDecimal cagInternalRecharge;
    /**
     * 外部充币
     */
    private BigDecimal usdtExternalRecharge;
    private BigDecimal catExternalRecharge;
    private BigDecimal cagExternalRecharge;
    /**
     * 激活费用退还
     */
    private BigDecimal usdtActiveReturn;
    private BigDecimal catActiveReturn;
    private BigDecimal cagActiveReturn;
    /**
     * 兑入得到
     */
    private BigDecimal usdtSmartContractBuyIncome;
    private BigDecimal catSmartContractBuyIncome;
    private BigDecimal cagSmartContractBuyIncome;
    /**
     * 兑出得到
     */
    private BigDecimal usdtSmartContractSellIncome;
    private BigDecimal catSmartContractSellIncome;
    private BigDecimal cagSmartContractSellIncome;
    /**
     * 活动奖励
     */
    private BigDecimal usdtActivityReward;
    private BigDecimal catActivityReward;
    private BigDecimal cagActivityReward;
    /**
     * 空投奖励
     */
    private BigDecimal usdtDropAward;
    private BigDecimal catDropAward;
    private BigDecimal cagDropAward;
    /**
     * 后台充值
     */
    private BigDecimal usdtBackgroundRecharge;
    private BigDecimal catBackgroundRecharge;
    private BigDecimal cagBackgroundRecharge;
    /**
     * 火箭转入钱包
     */
    private BigDecimal usdtRocketTransfer;
    private BigDecimal catRocketTransfer;
    private BigDecimal cagRocketTransfer;
    /**
     * 提现审核拒绝
     */
    private BigDecimal usdtWithdrawAuditRefuse;
    private BigDecimal catWithdrawAuditRefuse;
    private BigDecimal cagWithdrawAuditRefuse;
    /**
     * 兑出挂单超时
     */
    private BigDecimal usdtEntryOrdersTimeout;
    private BigDecimal catEntryOrdersTimeout;
    private BigDecimal cagEntryOrdersTimeout;
    /**
     * 出售商品
     */
    private BigDecimal usdtSellGoods;
    private BigDecimal catSellGoods;
    private BigDecimal cagSellGoods;
    /**
     * 内部提币
     */
    private BigDecimal usdtInternalCash;
    private BigDecimal catInternalCash;
    private BigDecimal cagInternalCash;
    /**
     * 外部提币
     */
    private BigDecimal usdtExternalCash;
    private BigDecimal catExternalCash;
    private BigDecimal cagExternalCash;
    /**
     * 激活算能支付
     */
    private BigDecimal usdtActivePay;
    private BigDecimal catActivePay;
    private BigDecimal cagActivePay;
    /**
     * 算能质押
     */
    private BigDecimal usdtPowerPledge;
    private BigDecimal catPowerPledge;
    private BigDecimal cagPowerPledge;
    /**
     * 兑出失去CAT
     */
    private BigDecimal usdtSmartContractSellDeduct;
    private BigDecimal catSmartContractSellDeduct;
    private BigDecimal cagSmartContractSellDeduct;
    /**
     * 兑入失去USDT
     */
    private BigDecimal usdtSmartContractBuyDeduct;
    private BigDecimal catSmartContractBuyDeduct;
    private BigDecimal cagSmartContractBuyDeduct;
    /**
     * 手续费CAG
     */
    private BigDecimal usdtSmartContractBuyFee;
    private BigDecimal catSmartContractBuyFee;
    private BigDecimal cagSmartContractBuyFee;
    /**
     * 钱包转出到火箭ex
     */
    private BigDecimal usdtTransferOutRocket;
    private BigDecimal catTransferOutRocket;
    private BigDecimal cagTransferOutRocket;
    /**
     * 购买商品
     */
    private BigDecimal usdtBuyGoods;
    private BigDecimal catBuyGoods;
    private BigDecimal cagBuyGoods;
    /**
     * 外部提币冻结
     */
    private BigDecimal usdtExternalCashFreeze;
    private BigDecimal catExternalCashFreeze;
    private BigDecimal cagExternalCashFreeze;
    /**
     * 算能质押冻结
     */
    private BigDecimal usdtPowerPledgeFreeze;
    private BigDecimal catPowerPledgeFreeze;
    private BigDecimal cagPowerPledgeFreeze;
    /**
     * 兑出冻结
     */
    private BigDecimal usdtSmartContractSellFreeze;
    private BigDecimal catSmartContractSellFreeze;
    private BigDecimal cagSmartContractSellFreeze;
    /**
     * 挂单手续费-CAG冻结
     */
    private BigDecimal usdtSmartContractSellFee;
    private BigDecimal catSmartContractSellFee;
    private BigDecimal cagSmartContractSellFee;
}
