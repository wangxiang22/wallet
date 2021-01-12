package com.xinlian.biz.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Song
 * @date 2020-07-25 14:28
 * @description
 */
@Data
public class CurrencyBalanceHourChangeModel {

    /**
     * 主键id
     */
    private Long id;
    /**
     * 币种id
     */
    private Long currencyId;
    /**
     * 币种编码
     */
    private String currencyCode;
    /**
     * 余额-币种数量
     */
    private BigDecimal balanceNum;
    /**
     * 冻结币种数量
     */
    private BigDecimal frozenNum;
    /**
     * 统计时间
     */
    private Date createtime;

}
