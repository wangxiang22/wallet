package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum ServerNodeWithdrawStatusEnum {

    WITHDRAW_YES(1,"可以提现"),
    WITHDRAW_NO(0,"不可以提现"),
    ;

    private int code;
    private String desc;

    ServerNodeWithdrawStatusEnum(int code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    /**
     * 判断是否可以内部转账
     * @param code
     * @return 可以内部转账 true 不可以内部转账 false
     */
    public static boolean checkIsWithdrawFlag(int code){
        return ServerNodeWithdrawStatusEnum.WITHDRAW_YES.code==code;
    }
}
