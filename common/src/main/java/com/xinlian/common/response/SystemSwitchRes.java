package com.xinlian.common.response;

import lombok.Data;

@Data
public class SystemSwitchRes {
    /**
     * 全局开关是否开启 - 0：关闭，1：开启
     */
    private String systemApplicationFlag;
    /**
     * 全局手机号码注册数量限制
     */
    private String mobileRegisterAmount;
    /**
     * 全局实名认证数量限制
     */
    private String authRegisterAmount;
}
