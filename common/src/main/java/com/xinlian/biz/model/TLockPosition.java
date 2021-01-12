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
 * 用户锁仓金额表
 * </p>
 *
 * @author wjf
 * @since 2020-01-17
 */
@Data
@TableName("t_lock_position")
public class TLockPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 节点id
     */
    @TableField("node_id")
    private Long nodeId;
    /**
     * 锁仓usdt数量
     */
    @TableField("usdt_amount")
    private BigDecimal usdtAmount;
    /**
     * 锁仓cat数量
     */
    @TableField("cat_amount")
    private BigDecimal catAmount;
    /**
     * 锁仓币种code
     */
    @TableField("amount_code")
    private Long amountCode;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

}
