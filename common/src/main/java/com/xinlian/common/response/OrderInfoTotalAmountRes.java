package com.xinlian.common.response;

import lombok.Data;

@Data
public class OrderInfoTotalAmountRes {
    /**
     * 卖家总出金CAT
     */
    private String sellerAltogetherOutAmount;
    /**
     * 卖家总入金USDT
     */
    private String sellerAltogetherInTotal;
    /**
     * 买家总出金USDT
     */
    private String buyerAltogetherOutTotal;
    /**
     * 买家总入金CAT
     */
    private String buyerAltogetherInAmount;
    /**
     * CAT出入金差额
     */
    private String catMargin;
    /**
     * USDT出入金差额
     */
    private String usdtMargin;
}
