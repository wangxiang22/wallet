package com.xinlian.biz.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class SimpleCurrencyTradeInfo {
    private Long uid;
    private Long tradeId;
    private Long currencyId;
    private String currencyCode;
    private BigDecimal tradeCurrencyNum;
    private Integer tradeType;
    private Integer tradeStatus;
    private String des;
    private Date createTime;
    private BigDecimal balanceNum;
    private Long walletId;
}
