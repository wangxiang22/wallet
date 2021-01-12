package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum CurrencyServiceFeeEnum {

    PERCENTAGE(1,"比例"),
    FIXATION(2,"固定值"),

    ;

    private int code;
    private String desc;

    CurrencyServiceFeeEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }


}
