package com.xinlian.common.dto;

import com.xinlian.common.response.OrderInfoTotalAmountRes;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderInfoTotalAmountDto {
    /**
     * 卖家总出金CAT
     */
    private BigDecimal sellerAltogetherOutAmount;
    /**
     * 卖家总入金USDT
     */
    private BigDecimal sellerAltogetherInTotal;
    /**
     * 买家总出金USDT
     */
    private BigDecimal buyerAltogetherOutTotal;
    /**
     * 买家总入金CAT
     */
    private BigDecimal buyerAltogetherInAmount;


    public OrderInfoTotalAmountRes orderInfoTotalAmountRes() {
        OrderInfoTotalAmountRes orderInfoTotalAmountRes = new OrderInfoTotalAmountRes();
        orderInfoTotalAmountRes.setSellerAltogetherOutAmount("-" + sellerAltogetherOutAmount);
        orderInfoTotalAmountRes.setSellerAltogetherInTotal("+" + sellerAltogetherInTotal);
        orderInfoTotalAmountRes.setBuyerAltogetherOutTotal("-" + buyerAltogetherOutTotal);
        orderInfoTotalAmountRes.setBuyerAltogetherInAmount("+" + buyerAltogetherInAmount);
        orderInfoTotalAmountRes.setCatMargin(sellerAltogetherOutAmount.subtract(buyerAltogetherInAmount).toString());
        orderInfoTotalAmountRes.setUsdtMargin(sellerAltogetherInTotal.subtract(buyerAltogetherOutTotal).toString());
        return orderInfoTotalAmountRes;
    }
}
