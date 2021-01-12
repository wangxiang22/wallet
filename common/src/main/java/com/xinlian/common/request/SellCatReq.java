package com.xinlian.common.request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SellCatReq {
    private BigDecimal amount;//数量  所出售CAT总和
    private BigDecimal price;//挂单价格 - USDT - （CAT单价）
    private BigDecimal total;//售出总价 - USDT 数量*挂单价格 =
    private String address;//卖家地址
    private String payPassword;//支付密码
    private Long uid;//用户uid
    private String phone;
    private String email;
    private Integer type;
    private String code;//验证码
    private String orderId;//订单号
    private String username;//用戶名
    private String sellerAddr;
    private Integer currencyId;
    private BigDecimal cagFee;//cag手续费
    private Integer countryCode;
    private Long buyerUid;
}
