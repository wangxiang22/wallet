package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum PledgeEnum {
    /**
     * 算能质押流水des字段的值
     */
    PLEDGE_MINING("des","保证金交纳"),
    ;

    /**
     * 请求参数名
     */
    private String parameterName;
    /**
     * 请求参数值
     */
    private String parameterValue;

    PledgeEnum(String parameterName,String parameterValue){
        this.parameterName=parameterName;
        this.parameterValue=parameterValue;
    }
}
