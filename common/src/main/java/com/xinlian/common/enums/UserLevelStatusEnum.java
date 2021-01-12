package com.xinlian.common.enums;


import lombok.Getter;

@Getter
public enum UserLevelStatusEnum {

    FREEZE(0,"冻结"),
    BASIC(1,"普通用户"),
    VIP(2,"会员"),
    ;

    private int code;
    private String desc;

    UserLevelStatusEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }
}
