package com.xinlian.common.enums;

import lombok.Getter;

/**
 * 地址分配状态
 */
@Getter
public enum AddressAllotStatusEnum {

    HAVE_NOT(0,"未分配"),
    ALREADY(1,"已分配"),



    //TRC20——USDT
    TRC_HAVE_NOT(1,"未分配"),
    TRC_ALREADY(2,"已分配"),



    ;

    private Integer code;
    private String des;

    AddressAllotStatusEnum(int code , String des){
        this.code=code;
        this.des=des;
    }

}
