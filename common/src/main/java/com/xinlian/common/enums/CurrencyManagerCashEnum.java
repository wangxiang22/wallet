package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum CurrencyManagerCashEnum {

    CASH_YES(1,"可以提现"),
    CASH_NO(2,"不可以提现"),
    ;

    private int code;
    private String desc;

    CurrencyManagerCashEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }
}
