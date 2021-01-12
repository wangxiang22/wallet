package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 账单分类表
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Data
@TableName("admin_bill_classify")
public class AdminBillClassify implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 账单分类名称
     */
    @TableField("bill_name")
    private String billName;
    /**
     * 展示状态 - 0：不展示，1：展示
     */
    @TableField("show_state")
    private Integer showState;

}
