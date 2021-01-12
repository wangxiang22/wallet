package com.xinlian.common.dto;

import com.xinlian.common.response.OrderInfoRealTimeRes;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Data
public class OrderInfoRealTimeDto {
    /**
     * 订单时间
     */
    private Long orderTime;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 卖家出金CAT
     */
    private BigDecimal sellerOutAmount;
    /**
     * 卖家入金USDT
     */
    private BigDecimal sellerInTotal;
    /**
     * 买家出金USDT
     */
    private BigDecimal buyerOutTotal;
    /**
     * 买家入金CAT
     */
    private BigDecimal buyerInAmount;


    public OrderInfoRealTimeRes orderInfoRealTimeRes() {
        OrderInfoRealTimeRes orderInfoRealTimeRes = new OrderInfoRealTimeRes();
        orderInfoRealTimeRes.setOrderTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderTime));
        orderInfoRealTimeRes.setOrderId(orderId);
        orderInfoRealTimeRes.setSellerOutAmount("-" + sellerOutAmount);
        orderInfoRealTimeRes.setSellerInTotal("+" + sellerInTotal);
        orderInfoRealTimeRes.setBuyerOutTotal("-" + buyerOutTotal);
        orderInfoRealTimeRes.setBuyerInAmount("+" + buyerInAmount);
        orderInfoRealTimeRes.setCatOutInDiffAmount(sellerOutAmount.subtract(buyerInAmount));
        orderInfoRealTimeRes.setUsdtOutInDiffAmount(sellerInTotal.subtract(buyerOutTotal));
        if (0 == orderInfoRealTimeRes.getCatOutInDiffAmount().compareTo(BigDecimal.ZERO)
                && 0 == orderInfoRealTimeRes.getUsdtOutInDiffAmount().compareTo(BigDecimal.ZERO)) {
            orderInfoRealTimeRes.setBillStatusName("正常");
        }else {
            orderInfoRealTimeRes.setBillStatusName("异常");
        }
        return orderInfoRealTimeRes;
    }
}
