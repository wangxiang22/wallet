package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum AdminTradeOrderQueryStatusEnum {

    APPLY("APPLY","申请"),
    UNDER_VIEW("UNDER_VIEW","审核中"),
    SUCCESS("SUCCESS","交易成功");

    private String code;
    private String desc;

    AdminTradeOrderQueryStatusEnum(String code, String desc){
        this.code=code;
        this.desc=desc;
    }
}
