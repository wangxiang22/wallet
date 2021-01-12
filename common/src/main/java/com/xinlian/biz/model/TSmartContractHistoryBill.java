package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.response.SmartContractHistoryBillRes;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * <p>
 * 智能合约历史账单
 * </p>
 *
 * @author lt
 * @since 2020-06-18
 */
@Data
@TableName("t_smart_contract_history_bill")
public class TSmartContractHistoryBill implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 账期
     */
    @TableField("bill_date")
    private Date billDate;
    /**
     * 卖家期初出金
     */
    @TableField("seller_initial_out_amount")
    private BigDecimal sellerInitialOutAmount;
    /**
     * 卖家期初入金
     */
    @TableField("seller_initial_in_amount")
    private BigDecimal sellerInitialInAmount;
    /**
     * 买家期初出金
     */
    @TableField("buyer_initial_out_amount")
    private BigDecimal buyerInitialOutAmount;
    /**
     * 买家期初入金
     */
    @TableField("buyer_initial_in_amount")
    private BigDecimal buyerInitialInAmount;
    /**
     * 卖家当日出金
     */
    @TableField("seller_today_out_amount")
    private BigDecimal sellerTodayOutAmount;
    /**
     * 卖家当日入金
     */
    @TableField("seller_today_in_amount")
    private BigDecimal sellerTodayInAmount;
    /**
     * 买家当日出金
     */
    @TableField("buyer_today_out_amount")
    private BigDecimal buyerTodayOutAmount;
    /**
     * 买家当日入金
     */
    @TableField("buyer_today_in_amount")
    private BigDecimal buyerTodayInAmount;
    /**
     * 卖家期末出金
     */
    @TableField("seller_end_out_amount")
    private BigDecimal sellerEndOutAmount;
    /**
     * 卖家期末入金
     */
    @TableField("seller_end_in_amount")
    private BigDecimal sellerEndInAmount;
    /**
     * 买家期末出金
     */
    @TableField("buyer_end_out_amount")
    private BigDecimal buyerEndOutAmount;
    /**
     * 买家期末入金
     */
    @TableField("buyer_end_in_amount")
    private BigDecimal buyerEndInAmount;
    /**
     * 账单状态 1 正确  2 异常
     */
    @TableField("bill_status")
    private Integer billStatus;
    /**
     * 期末cat出入金差额
     */
    @TableField("cat_outIn_diff_amount")
    private BigDecimal catOutInDiffAmount;
    /**
     * 期末usdt出金差额
     */
    @TableField("usdt_outIn_diff_amount")
    private BigDecimal usdtOutInDiffAmount;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    public SmartContractHistoryBillRes historyBillRes() {
        SmartContractHistoryBillRes historyBillRes = new SmartContractHistoryBillRes();
        historyBillRes.setBillDate(billDate);
        historyBillRes.setSellerInitialOutInAmount(getAmount(sellerInitialOutAmount, CurrencyEnum.CAT.getCurrencyCode(),sellerInitialInAmount,CurrencyEnum.USDT.getCurrencyCode()));
        historyBillRes.setBuyerInitialOutInAmount(getAmount(buyerInitialOutAmount, CurrencyEnum.USDT.getCurrencyCode(),buyerInitialInAmount,CurrencyEnum.CAT.getCurrencyCode()));
        historyBillRes.setSellerTodayOutInAmount(getAmount(sellerTodayOutAmount, CurrencyEnum.CAT.getCurrencyCode(),sellerTodayInAmount,CurrencyEnum.USDT.getCurrencyCode()));
        historyBillRes.setBuyerTodayOutInAmount(getAmount(buyerTodayOutAmount, CurrencyEnum.USDT.getCurrencyCode(),buyerTodayInAmount,CurrencyEnum.CAT.getCurrencyCode()));
        historyBillRes.setSellerEndOutInAmount(getAmount(sellerEndOutAmount, CurrencyEnum.CAT.getCurrencyCode(),sellerEndInAmount,CurrencyEnum.USDT.getCurrencyCode()));
        historyBillRes.setBuyerEndOutInAmount(getAmount(buyerEndOutAmount, CurrencyEnum.USDT.getCurrencyCode(),buyerEndInAmount,CurrencyEnum.CAT.getCurrencyCode()));
        historyBillRes.setEndOutInDiffAmount(getAmount(catOutInDiffAmount, CurrencyEnum.CAT.getCurrencyCode(),usdtOutInDiffAmount,CurrencyEnum.USDT.getCurrencyCode()));
        //账单状态 1 正常  2 异常
        switch (billStatus) {
            case 1:
                historyBillRes.setBillStatusName("正常");
                break;
            case 2:
                historyBillRes.setBillStatusName("异常");
                break;
        }
        return historyBillRes;
    }

    private String getAmount(BigDecimal outAmount,String outCurrencyName,BigDecimal inAmount,String inCurrencyName) {
        String outDecimalStr = outAmount.setScale(4, ROUND_HALF_UP).toString();
        String inDecimalStr = inAmount.setScale(4, ROUND_HALF_UP).toString();
        return outDecimalStr + outCurrencyName + "/" + inDecimalStr + inCurrencyName;
    }
}
