package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDetailRes {
    /**
     * 账单分类id
     */
    private Long billId;
    /**
     * 账单分类名称
     */
    private String billName;
    /**
     * 账单进账金额
     */
    private BigDecimal billTakeInAmount;
    /**
     * 账单出账金额
     */
    private BigDecimal billExpenditureAmount;
    /**
     * 账单差额
     */
    private BigDecimal billDifferenceAmount;
}
