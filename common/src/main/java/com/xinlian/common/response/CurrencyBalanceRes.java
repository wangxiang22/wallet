package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyBalanceRes {
    private String icon;
    private BigDecimal frozen;
    private BigDecimal total;
    private String coinname;
}
