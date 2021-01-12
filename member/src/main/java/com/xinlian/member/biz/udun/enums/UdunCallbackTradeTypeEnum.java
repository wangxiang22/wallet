package com.xinlian.member.biz.udun.enums;

import lombok.Getter;

@Getter
public enum UdunCallbackTradeTypeEnum {


    RECHARGE_CALLBACK(1,"充币回调"),
    WITHDRAW_CALLBACK(2,"提币回调"),



    ;
    private int code;
    private String desc;

    UdunCallbackTradeTypeEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
