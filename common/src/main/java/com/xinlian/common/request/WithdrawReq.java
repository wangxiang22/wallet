package com.xinlian.common.request;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class WithdrawReq {
    private Long uid;
    private BigDecimal amount;
    private String coinName;
}
