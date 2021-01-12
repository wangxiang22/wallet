package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum HedgeCustomerWalletEnum {
    /**
     * 冻结算力地球挖矿保证金-默认CAT
     */
    MINING_DEPOSIT(1,"MINING_DEPOSIT"),
    ;

    private Integer code;
    private String typeCode;
    HedgeCustomerWalletEnum(Integer code,String typeCode){
        this.code=code;
        this.typeCode=typeCode;
    }
}
