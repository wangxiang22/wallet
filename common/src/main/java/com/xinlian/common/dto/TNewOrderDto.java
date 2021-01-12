package com.xinlian.common.dto;

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
public class TNewOrderDto extends BaseRowModel implements Serializable {

    @ExcelProperty("id")
    @ExcelIgnore
    private Long id;

    /**
     * 用户uid
     */
    @ExcelProperty("用户uid")
    private Long uid;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String goodsName;

    /**
     * 购买数量
     */
    @ExcelProperty("购买数量")
    private Integer amount;

    /**
     * 购买单价
     */
    @ExcelProperty("购买单价")
    private BigDecimal price;

    /**
     * 订单号
     */
    @ExcelProperty("订单号")
    private String orderNo;

    /**
     * 收货地址
     */
    @ExcelProperty("收货地址")
    private String address;

    /**
     * 收件人手机号
     */
    @ExcelProperty("收件人手机号")
    private String phone;

    /**
     * 收件人
     */
    @ExcelProperty("收件人")
    private String userName;
    /**
     * 链区
     */
    @ExcelProperty("链区")
    private String chainName;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;
    /**
     * 物流公司名称
     */
    @ExcelProperty("物流公司名称")
    private String expressName;

    /**
     * 物流单号
     */
    @ExcelProperty("物流单号")
    private String expressCode;

}
