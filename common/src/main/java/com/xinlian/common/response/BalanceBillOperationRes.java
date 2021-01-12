package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceBillOperationRes {
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 账单分类名称
     */
    private String billName;
    /**
     * 对冲数量
     */
    private BigDecimal hedgeAmount;
    /**
     * 对冲时间
     */
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date hedgeTime;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 操作时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date operationTime;
}
