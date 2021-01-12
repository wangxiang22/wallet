package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum WalletTradeSystemCodeEnum {




    ADMIN_TRADE("ADMIN_TRADE","ADMIN_交易"),
    APP_TRADE("APP_TRADE","APP_交易"),
    APP_TRADE_TRC("APP_TRADE_TRC","APP_交易_TRC"),
    APP_H5_SMART_CONTRACT("H5_SC","智能合约H5_交易"),





    ;


    private String code;
    private String desc;

    WalletTradeSystemCodeEnum(String code , String desc){
        this.code = code;
        this.desc = desc;
    }


}
