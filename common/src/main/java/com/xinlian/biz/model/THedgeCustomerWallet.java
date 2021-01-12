package com.xinlian.biz.model;

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
 * 冻结客户资产记录表
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
@Data
@TableName("t_hedge_customer_wallet")
public class THedgeCustomerWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 冻结值
     */
    @TableField("hedge_num")
    private BigDecimal hedgeNum;
    /**
     * 状态-0：冻结，1：解冻
     */
    private Integer status;
    /**
     * 币种id
     */
    @TableField("currency_id")
    private Long currencyId;
    /**
     * 币种编码
     */
    @TableField("currency_code")
    private String currencyCode;
    /**
     * 操作类型-引发冻结原因编码
     */
    @TableField("ope_type")
    private String opeType;
    /**
     * 冻结时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("hedge_time")
    private Date hedgeTime;
    /**
     * 待解冻时间点
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("stay_unfreeze_time")
    private Date stayUnfreezeTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("update_time")
    private Date updateTime;

}
