package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum RechargeOperTypeEnum {




    MALE_CHAIN_RECHARGE("MALE_CHAIN_RECHARGE","maleChain充值"),

    UDUN_RECHARGE("UDUN_RECHARGE","U盾充值"),
    ;

    private String operType;
    private String operDesc;

    RechargeOperTypeEnum(String operType , String operDesc){
        this.operType = operType;
        this.operDesc = operDesc;
    }



}
