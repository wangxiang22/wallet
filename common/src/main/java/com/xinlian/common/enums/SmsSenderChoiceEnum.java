package com.xinlian.common.enums;


import lombok.Getter;

@Getter
public enum SmsSenderChoiceEnum {

    SMS_CHUANGLAN("SMS_CHUANGLAN","创蓝"),
    SMS_CHUANGLAN_ABROAD("SMS_CHUANGLAN_ABROAD","创蓝国际"),

    SMS_CHUANGRUI("SMS_CHUANGRUI","创瑞"),
    SMS_CHUANGRUI_ABROAD("SMS_CHUANGRUI_ABROAD","创瑞国际"),


    SMS_ALI_YUN("SMS_ALI_YUN","阿里云国内"),
    SMS_ALI_YUN_ABROAD("SMS_ALI_YUN_ABROAD","阿里云国际"),


    SMS_YUNPIAN("SMS_YUNPIAN","云片国内"),
    SMS_YUNPIAN_ABROAD("SMS_YUNPIAN_ABROAD","云片国际"),

    SMS_SEND_CLOUD("SMS_SEND_CLOUD","sendCloud国内"),
    SMS_SEND_CLOUD_ABROAD("SMS_SEND_CLOUD_ABROAD","sendCloud国际"),

    ;


    private String code;
    private String desc;

    SmsSenderChoiceEnum(String code, String desc){
        this.code=code;
        this.desc=desc;
    }

}
