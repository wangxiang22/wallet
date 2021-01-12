package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 统计订单
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-15
 */
@Data
public class StatisticsTradeOrderModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 对账日
     */
    private String reconcileDate;
    /**
     * 币种id
     */
    private Integer currencyId;
    /**
     * 币种code
     */
    private String currencyCode;
    /**
     * 节点id
     */
    private Long serverNodeId;
    /**
     * 节点名称
     */
    private String serverNodeName;
    /**
     * 统计数值
     */
    private BigDecimal statisticsNumber;
    /**
     * 统计类目
     */
    private String statisticsType;

    /**
     * 清算日期
     */
    private Date clearingDatetime;
    /**
     * 创建日期
     */
    private Date createTime;







}
