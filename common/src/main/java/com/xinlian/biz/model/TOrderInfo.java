package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.*;
import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
@Data
@TableName("t_order_info")
public class TOrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    /**
     * cat数量
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * cat单价
     */
    @TableField("price")
    private BigDecimal price;
    /**
     * 总usdt量
     */
    @TableField("total")
    private BigDecimal total;
    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;
    /**
     * 结束时间
     */
    @TableField("end_time")
    private Long endTime;
    /**
     * cat余额
     */
    @TableField("cat")
    private BigDecimal cat;
    /**
     * usdt余额
     */
    @TableField("usdt")
    private BigDecimal usdt;
    /**
     * 地址
     */
    @TableField("addr")
    private String addr;
    /**
     * 交易方向0买1卖
     */
    @TableField("trade_type")
    private Integer tradeType;

    @TableField("uid")
    private Long uid;

}
