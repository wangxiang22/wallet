package com.xinlian.member.biz.udun.enums;

import lombok.Getter;

@Getter
public enum CallBackBodyStatusEnum {

    WAIT_AUDIT(0,"待审核"),
    AUDIT_SUCCESS(1,"审核成功"),
    AUDIT_REFUSED(2,"审核驳回"),
    COMPLETED(3,"交易成功"),
    TRADE_FAIL(4,"交易失败");

    private int code;
    private String desc;

    CallBackBodyStatusEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
