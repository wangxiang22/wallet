package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderInfoRealTimeRes {
    /**
     * 订单时间
     */
    private String orderTime;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 卖家出金CAT
     */
    private String sellerOutAmount;
    /**
     * 卖家入金USDT
     */
    private String sellerInTotal;
    /**
     * 买家出金USDT
     */
    private String buyerOutTotal;
    /**
     * 买家入金CAT
     */
    private String buyerInAmount;
    /**
     * 出入金差额CAT
     */
    private BigDecimal catOutInDiffAmount;
    /**
     * 出入金差额USDT
     */
    private BigDecimal usdtOutInDiffAmount;
    /**
     * 账单状态:  正常   异常
     */
    private String billStatusName;
}
