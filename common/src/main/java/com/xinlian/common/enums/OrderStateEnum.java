package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum OrderStateEnum {

    //0.等待交易 1.已过期 2.已成交
    APPLY(0,"等待交易"),
    TIME_OUT(1,"已过期"),
    FINISH(2,"已成交"),

    ;


    private Integer code;
    private String desc;

    OrderStateEnum(Integer code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code){
        return Stream.of(OrderStateEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null).getDesc();
    }
}
