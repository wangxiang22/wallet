package com.xinlian.common.request;

import lombok.Data;

@Data
public class ExchangeBalanceReq {
    private Long uid;//交易所uid
    private String coinName;//币名称
}
