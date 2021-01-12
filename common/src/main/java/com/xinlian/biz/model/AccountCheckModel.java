package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 财务核查表
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-15
 */
@Data
public class AccountCheckModel implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String params = "{currencyCode:GPT/USDT/CAG/CAT;serverNodeId:1等;reconcileDateStartDate:开始日期-reconcileDateEndDate:结束日期}";

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

    /***查询***/
    private Integer tradeType;
    private String des;
    private String staticsDate;
    private String [] otherStatistics;

    /**列表查询**/
    private String reconcileDateStartDate;
    private String reconcileDateEndDate;


    @Override
    public String toString() {
        return "TAccountCheck{" +
        ", id=" + id +
        ", reconcileDate=" + reconcileDate +
        ", currencyId=" + currencyId +
        ", currencyCode=" + currencyCode +
        ", serverNodeId=" + serverNodeId +
        ", serverNodeName=" + serverNodeName +
        ", currentTotalCurrencyNum=" + currentTotalCurrencyNum +
        ", offSiteRechargeNum=" + offSiteRechargeNum +
        ", offSiteWithdraw=" + offSiteWithdraw +
        ", fromRocketNum=" + fromRocketNum +
        ", toRocketNum=" + toRocketNum +
        ", otherRecorded=" + otherRecorded +
        ", otherChargeOff=" + otherChargeOff +
        ", clearingDatetime=" + clearingDatetime +
        ", createTime=" + createTime +
        "}";
    }
}
