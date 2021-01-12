package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 财务核查表
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-25
 */
@Data
public class TopNodeTradeDataResponse {

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
     * 当期总额
     */
    private BigDecimal currentTotalCurrencyNum;
    /**
     * 站外充值
     */
    private BigDecimal offSiteRechargeNum;
    /**
     * 站外提现
     */
    private BigDecimal offSiteWithdraw;
    /**
     * 来自火箭数值
     */
    private BigDecimal fromRocketNum;
    /**
     * 转到火箭数值
     */
    private BigDecimal toRocketNum;
    /**
     * 其他入账
     */
    private BigDecimal otherRecorded;
    /**
     * 其他出账
     */
    private BigDecimal otherChargeOff;
    /**
     * 清算日期
     */
    private Date clearingDatetime;
    /**
     * 创建日期
     */
    private Date createTime;


    //汇总to顶级节点
    private String parentIds;
    private Long parentId;

    private List<TopNodeTradeDataResponse> childList;


}
