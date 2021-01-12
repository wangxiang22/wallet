package com.xinlian.common.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CatWalletPayReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @NotNull(message = "支付金额不能为空")
    private String payAmount;

    @NotNull(message = "区域不能为空")
    private Integer countryCode;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    @NotBlank(message = "手机号验证码不能为空")
    private String code;// 验证码

    @NotBlank(message = "交易密码不能为空")
    private String dealPsw;// 交易密码

    @NotBlank(message = "支付结果回调地址不能为空")
    private String callback;// 支付结果回调地址

    private Long uid;
    private Long nodeId;

    private Integer type;

}
