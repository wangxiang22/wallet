package com.xinlian.admin.server.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCheckVo {

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
    private String clearingDatetime;
    /**
     * 创建日期
     */
    private String createTime;
}
