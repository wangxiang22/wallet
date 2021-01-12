package com.xinlian.common.request;

import lombok.Data;

@Data
public class OrderReq {

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格
     */
    private String spec = "默认规格";

    /**
     * 收件地址
     */
    private String address;

    /**
     * 收件电话
     */
    private String phone;

    /**
     * 收件人
     */
    private String userName;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 支付密码
     */
    private String password;

    /**
     * 短信验证码
     */
    private String code;

    /**
     * 链区
     */
    private String chainName;
}
