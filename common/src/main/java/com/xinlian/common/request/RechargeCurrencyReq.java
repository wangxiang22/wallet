package com.xinlian.common.request;

import lombok.Data;

@Data
public class RechargeCurrencyReq {
    //1提现 2充值
    private Integer delType;
}
