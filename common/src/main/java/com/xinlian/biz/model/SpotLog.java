package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author lx
 * @since 2020-07-01
 */
@Data
@TableName("spot_log")
public class SpotLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("ip")
    private String ip;
    /**
     * 设备
     */
    @TableField("device")
    private String device;
    /**
     * 订单号
     */
    @TableField("order_id")
    private String orderId;
    /**
     * uid
     */
    @TableField("uid")
    private String uid;
    @TableField("create_time")
    private Date createTime;

    /**涉及交易流水id集合**/
    @TableField("involve_trade_order_ids")
    private String involveTradeOrderIds;
    //ps: seller : 产生一笔CAT交易 - 一笔CAG费用
    //buyer : 产生一笔USDT（buyer减）、一笔CAT 、一笔USDT(seller加)

    //交易方 - seller / buyer
    @TableField("trade_type")
    private String tradeType;

}
