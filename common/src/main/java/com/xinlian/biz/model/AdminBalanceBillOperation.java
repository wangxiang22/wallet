package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 平账操作记录表
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Data
@TableName("admin_balance_bill_operation")
public class AdminBalanceBillOperation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 币种id
     */
    @TableField("currency_id")
    private Long currencyId;
    /**
     * 币种名称
     */
    @TableField("currency_name")
    private String currencyName;
    /**
     * 账单分类名称
     */
    @TableField("bill_name")
    private String billName;
    /**
     * 对冲数量
     */
    @TableField("hedge_amount")
    private BigDecimal hedgeAmount;
    /**
     * 对冲时间
     */
    @TableField("hedge_time")
    private Date hedgeTime;
    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;
    /**
     * 操作人
     */
    @TableField("operator")
    private String operator;
    /**
     * 操作时间
     */
    @TableField("operation_time")
    private Date operationTime;

}
