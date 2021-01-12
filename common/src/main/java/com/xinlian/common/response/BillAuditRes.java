package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillAuditRes {
    /**
     * 进账金额
     */
    private BigDecimal takeInAmount;
    /**
     * 平账进账金额
     */
    private BigDecimal balanceBillTakeInAmount;
    /**
     * 总进账金额
     */
    private BigDecimal totalTakeInAmount;
    /**
     * 出账金额
     */
    private BigDecimal expenditureAmount;
    /**
     * 平账出账金额
     */
    private BigDecimal balanceBillExpenditureAmount;
    /**
     * 总出账金额
     */
    private BigDecimal totalExpenditureAmount;
    /**
     * 差额
     */
    private BigDecimal differenceAmount;

    /**
     * 质押人数（包含申请、拒绝、通过三个状态）
     */
    private Long pledgeMiningPopulation;

    /**
     * 质押金额（包含申请、拒绝、通过三个状态）
     */
    private BigDecimal pledgeMiningAmount;
}
