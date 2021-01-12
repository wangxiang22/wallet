package com.xinlian.biz.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 购买口罩订单实体类
 * </p>
 *
 * @author cms
 * @since 2020-08-18
 */
@Data
@TableName("t_new_order")
public class TNewOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 商品名称
     */
    @TableField(value = "goods_name")
    private String goodsName;

    /**
     * 购买数量
     */
    private Integer amount;

    /**
     * 购买单价
     */
    private BigDecimal price;

    /**
     * 订单号
     */
    @TableField(value = "order_no")
    private String orderNo;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收件人手机号
     */
    private String phone;

    /**
     * 收件人
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 订单状态 0已付款  1已发货  2已完成
     */

    private Integer status = 0;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 发货时间
     */
    @TableField(value = "send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 完成时间
     */
    @TableField(value = "finish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date finishTime;

    /**
     * 物流公司代码
     */
    @TableField(value = "express_comId")
    private String expressComId;

    /**
     * 物流公司名称
     */
    @TableField("express_name")
    private String expressName;

    /**
     * 物流单号
     */
    @TableField(value = "express_code")
    private String expressCode;

    /**
     * 是否导出
     */
    @TableField(value = "is_import")
    private Integer isImport = 0;

    /**
     * 链区
     */
    @TableField(value = "chain_name")
    private String chainName;
}
