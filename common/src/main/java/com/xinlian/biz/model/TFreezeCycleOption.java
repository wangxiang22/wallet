package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 冻结周期配置表
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
@Data
@TableName("t_freeze_cycle_option")
public class TFreezeCycleOption implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 冻结原因编码
     */
    @TableField("freeze_type_code")
    private String freezeTypeCode;
    /**
     * 冻结周期（单位：天）
     */
    @TableField("freeze_cycle")
    private Integer freezeCycle;
    /**
     * 冻结金额
     */
    @TableField("freeze_amount")
    private BigDecimal freezeAmount;
    /**
     * 冻结原因备注
     */
    @TableField("freeze_note")
    private String freezeNote;

}
