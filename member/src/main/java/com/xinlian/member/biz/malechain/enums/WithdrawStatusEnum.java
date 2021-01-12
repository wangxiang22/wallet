package com.xinlian.member.biz.malechain.enums;

import lombok.Getter;

@Getter
public enum WithdrawStatusEnum {

    FAILURE(0,"失败"),
    SUCCESS(1,"成功"),
    PENDING(2,"交易确认中"),



    ;

    private Integer code;
    private String desc;

    WithdrawStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
