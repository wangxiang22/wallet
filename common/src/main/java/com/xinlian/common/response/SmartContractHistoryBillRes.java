package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SmartContractHistoryBillRes {
    /**
     * 账期
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date billDate;
    /**
     * 卖家期初出入金（CAT/USDT）
     */
    private String sellerInitialOutInAmount;
    /**
     * 买家期初出入金（USDT/CAT）
     */
    private String buyerInitialOutInAmount;
    /**
     * 卖家当日出入金（CAT/USDT）
     */
    private String sellerTodayOutInAmount;
    /**
     * 买家当日出入金（USDT/CAT）
     */
    private String buyerTodayOutInAmount;
    /**
     * 卖家期末出入金（CAT/USDT）
     */
    private String sellerEndOutInAmount;
    /**
     * 买家期末出入金（USDT/CAT）
     */
    private String buyerEndOutInAmount;
    /**
     * 期末出入金差额（CAT/USDT）
     */
    private String endOutInDiffAmount;
    /**
     * 账单状态 1 正常  2 异常
     */
    private String billStatusName;
}
