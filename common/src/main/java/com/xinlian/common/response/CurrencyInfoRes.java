package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyInfoRes {
    private Long id;
    private String currencyCode;
    private String currencyName;
    private String thumb;
    private BigDecimal dollar;
}
