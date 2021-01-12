package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum VendorSmsConfigEnum {

    APP_CHUANGRUI_INLAND_SMS("APP_CHUANGRUI_INLAND_SMS","创瑞国内短信配置"),

    APP_CHUANGRUI_ABROAD_SMS("APP_CHUANGRUI_ABROAD_SMS","创瑞国际短信配置"),

    APP_INLAND_SMS("APP_INLAND_SMS","国内短信配置"),

    APP_ABROAD_SMS("APP_ABROAD_SMS","国际短信配置"),


    APP_SEND_CLOUD_INLAND_SMS("APP_SEND_CLOUD_INLAND_SMS","sendCloud国内短信配置"),
    APP_SEND_CLOUD_ABROAD_SMS("APP_SEND_CLOUD_ABROAD_SMS","sendCloud国际短信配置"),


    APP_ALIYUN_INLAND_SMS("APP_ALIYUN_INLAND_SMS","阿里云国内短信配置"),
    APP_ALIYUN_ABROAD_SMS("APP_ALIYUN_ABROAD_SMS","阿里云国际短信配置"),


    APP_YUNPIAN_INLAND_SMS("APP_YUNPIAN_INLAND_SMS","云片国内短信配置"),
    APP_YUNPIAN_ABROAD_SMS("APP_YUNPIAN_ABROAD_SMS","云片云国际短信配置"),

    ;

    private String belongsSystemCode;
    private String belongsSystemDesc;

    VendorSmsConfigEnum(String belongsSystemCode, String belongsSystemDesc){
        this.belongsSystemCode=belongsSystemCode;
        this.belongsSystemDesc=belongsSystemDesc;
    }

}
