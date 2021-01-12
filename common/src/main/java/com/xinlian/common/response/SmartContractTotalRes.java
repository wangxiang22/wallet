package com.xinlian.common.response;

import lombok.Data;

@Data
public class SmartContractTotalRes {
    /**
     * 卖家总出金
     */
    private String sellerTotalOutAmount;
    /**
     * 卖家总入金
     */
    private String sellerTotalInAmount;
    /**
     * 买家总出金
     */
    private String buyerTotalOutAmount;
    /**
     * 买家总入金
     */
    private String buyerTotalInAmount;
    /**
     * CAT出入金差额
     */
    private String catMargin;
    /**
     * USDT出入金差额
     */
    private String usdtMargin;
}
