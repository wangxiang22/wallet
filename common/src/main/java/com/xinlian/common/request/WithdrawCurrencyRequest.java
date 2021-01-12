package com.xinlian.common.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 提币请求request
 */
@Data
public class WithdrawCurrencyRequest {
    //token -
    @Deprecated
    private String uid;
    //币id
    private String coin_id;
    //提币地址
    private String address;
    //地址id
    private String address_id;
    //memo -安卓说空
    private String memo;
    //提币数量
    private String num;
    //code - 写死了
    private String code;
    //短信验证码
    private String smsCode;
    //支付密码
    private String deal_psw;

    private Long userId;

    private Long serverNodeId;
    //提币通道
    private String withdrawChannel;

    //2020-02-09 new add 邮箱,手机 接收验证码 - SMS,EMAIL
    private String waitVerifyType;

    public static final String PARAMS =
            "{uid:token,coin_id:币id,address:提币地址,"
            +"memo:有前提条件,num:提币数量,code:前端写死,deal_psw:支付密码"
            +",waitVerifyType:SMS 邮箱-EMAIL 手机接收验证码"
            +"}";

    public static final String EMAIL = "EMAIL";
    public static final String SMS = "SMS";

    public static void main(String[] args) {
        WithdrawCurrencyRequest withdrawCurrencyRequest = new WithdrawCurrencyRequest();
        withdrawCurrencyRequest.setCoin_id("1");
        withdrawCurrencyRequest.setAddress("1");
        withdrawCurrencyRequest.setAddress_id("1");
        withdrawCurrencyRequest.setNum("1");
        withdrawCurrencyRequest.setCode("1");
        withdrawCurrencyRequest.setDeal_psw("1");
        System.err.println(JSONObject.toJSONString(withdrawCurrencyRequest));
    }
}

