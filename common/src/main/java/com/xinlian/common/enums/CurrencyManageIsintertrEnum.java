package com.xinlian.common.enums;

import lombok.Getter;

/**
 * 币种管理下的 内部转账判断枚举
 */
@Getter
public enum CurrencyManageIsintertrEnum {

    INTERNAL_TRANSFER_YES(1,"是"),
    INTERNAL_TRANSFER_NO(2,"否"),
    ;

    private int code;
    private String desc;

    CurrencyManageIsintertrEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 判断是否可以内部转账
     * @param code
     * @return 可以内部转账 true 不可以内部转账 false
     */
    public static boolean checkIsIntertrFlag(int code){
        return CurrencyManageIsintertrEnum.INTERNAL_TRANSFER_YES.code==code;
    }
}
