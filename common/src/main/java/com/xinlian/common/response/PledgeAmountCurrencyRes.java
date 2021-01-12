package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 质押扣款所需参数
 */
@Data
public class PledgeAmountCurrencyRes {
    //质押金额
    private BigDecimal pledgeAmount;
    //币种编码名称
    private String currencyCode;
}
