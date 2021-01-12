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
 * @since 2020-06-06
 */
@Data
@TableName("t_order")
public class TOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 订单id
     */
    @TableField("order_id")
    private String orderId;
    /**
     * 卖方uid
     */
    @TableField("seller_uid")
    private Long sellerUid;
    /**
     * 买方uid
     */
    @TableField("buyer_uid")
    private Long buyerUid;
    /**
     * 交易cat数量
     */
    @TableField("amount")
    private BigDecimal amount;
    /**
     * 单位cat价格
     */
    @TableField("price")
    private BigDecimal price;
    /**
     * 交易总金额(usdt)
     */
    @TableField("total")
    private BigDecimal total;
    /**
     * 卖家地址
     */
    @TableField("seller_addr")
    private String sellerAddr;
    /**
     * 买家地址
     */
    @TableField("buyer_addr")
    private String buyerAddr;
    /**
     * 0.等待交易 1.已过期 2.已成交
     */
    @TableField("state")
    private Integer state;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;
    /**
     * 结束时间
     */
    @TableField("end_time")
    private Long endTime;
    @TableField(exist = false)
    private Long timeOutTime;
    @TableField(exist = false)
    private Integer type;//1买2卖

    @TableField("seller_phone")
    private String sellerPhone;
    @TableField("seller_username")
    private String sellerUsername;
    @TableField("buyer_username")
    private String buyerUsername;
    @TableField("buyer_phone")
    private String buyerPhone;
    @TableField("cag_fee")
    private BigDecimal cagFee;


}
