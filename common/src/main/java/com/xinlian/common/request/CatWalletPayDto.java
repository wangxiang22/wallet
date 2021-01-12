package com.xinlian.common.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CatWalletPayDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotBlank(message = "支付金额不能为空")
    private String payAmount;

    @NotBlank(message = "溯源服务标题（开通溯源，续约溯源）不能为空")
    private String sourceTitle;

    @NotBlank(message = "溯源服务类型不能为空")
    private String sourceType;

    @NotNull(message = "购买时长（单位：天数）不能为空")
    @Max(value = 36500, message = "购买时长（单位：天数）最大值是36500")
    @Min(value = 1, message = "购买时长（单位：天数）最小值是1")
    private Long purchaseTime;

    private String callback;

    @NotNull(message = "二维码过期时间(秒)")
    private Long qrCodeTimeOut;

    @NotNull(message = "订单过期时间（秒）")
    private Long orderTimeOut;

}
