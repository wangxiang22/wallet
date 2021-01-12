package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.response.CurrencyInfoRes;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@TableName("t_currency_manage")
public class TCurrencyManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 关联币种id
     */
    @TableField("currency_id")
    private Long currencyId;
    /**
     * 币名称
     */
    @TableField("coinname")
    private String coinname;

    @TableField("dollar")
    private BigDecimal dollar;
    /**
     * 图标
     */
    @TableField("icon")
    private String icon;
    /**
     * 提现手续费
     */
    @TableField("cash_fee")
    private BigDecimal cashFee;
    /**
     * 提现手续费类型: 1、比例 2、固定值
     */
    @TableField("cashfee_status")
    private Integer cashfeeStatus;
    /**
     * 最低提现手续费
     */
    @TableField("cashfee_min")
    private BigDecimal cashfeeMin;
    /**
     * 内部转账手续费
     */
    @TableField("inside_trade_fee")
    private BigDecimal insideTradeFee;
    /**
     * 内部转账手续费类型: 1.比例  2.固定值
     */
    @TableField("inside_trade_status")
    private Integer insideTradeStatus;
    /**
     * 最低内部转账手续费
     */
    @TableField("inside_trade_min")
    private BigDecimal insideTradeMin;

    @TableField("usdt_rate")
    private BigDecimal usdtRate;
    /**
     * 是否允许提现 1-是 2-否
     */
    @TableField("cash")
    private Integer cash;
    /**
     * 是否允许充值：1:是；2:否
     */
    @TableField("recharge")
    private Integer recharge;
    /**
     * 排序
     */
    @TableField("orderbys")
    private Integer orderbys;
    /**
     * 1、可用 2、不可用
     */
    @TableField("status")
    private Integer status;
    /**
     * 手动充值地址
     */
    @TableField("recharge_address")
    private String rechargeAddress;
    /**
     * 提现手续费币ID
     */
    @TableField("cash_fee_currency_id")
    private Integer cashFeeCurrencyId;
    /**
     * 内部转账手续费扣币ID
     */
    @TableField("convert_fee_currency_id")
    private Integer convertFeeCurrencyId;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("nodeurl")
    private String nodeurl;
    /**
     * 是否允许内部转账 1、是 2、否
     */
    @TableField("isintertr")
    private Integer isintertr;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 创建人
     */
    @TableField("creator")
    private String creator;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 更新者
     */
    @TableField("updator")
    private String updator;
    //单日充值最大允许次数
    @TableField("desposit_time")
    private Integer despositTime;
    //单日提现最大允许次数
    @TableField("withdraw_time")
    private Integer withdrawTime;
    //单日允许提现总额度
    @TableField("withdraw_amount")
    private BigDecimal withdrawAmount;
    //单日允许充值总额度
    @TableField("desposit_amount")
    private BigDecimal despositAmount;
    //单次冲提最大额度
    @TableField("withdraw_single_amount")
    private BigDecimal withdrawSingleAmount;
    @TableField("ex_to_wallet")
    private Integer exToWallet;
    @TableField("wallet_to_ex")
    private Integer walletToEx;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCoinname() {
        return coinname;
    }

    public void setCoinname(String coinname) {
        this.coinname = coinname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public BigDecimal getCashFee() {
        return cashFee;
    }

    public void setCashFee(BigDecimal cashFee) {
        this.cashFee = cashFee;
    }

    public Integer getCashfeeStatus() {
        return cashfeeStatus;
    }

    public void setCashfeeStatus(Integer cashfeeStatus) {
        this.cashfeeStatus = cashfeeStatus;
    }

    public BigDecimal getCashfeeMin() {
        return cashfeeMin;
    }

    public void setCashfeeMin(BigDecimal cashfeeMin) {
        this.cashfeeMin = cashfeeMin;
    }

    public BigDecimal getInsideTradeFee() {
        return insideTradeFee;
    }

    public void setInsideTradeFee(BigDecimal insideTradeFee) {
        this.insideTradeFee = insideTradeFee;
    }

    public Integer getInsideTradeStatus() {
        return insideTradeStatus;
    }

    public void setInsideTradeStatus(Integer insideTradeStatus) {
        this.insideTradeStatus = insideTradeStatus;
    }

    public BigDecimal getInsideTradeMin() {
        return insideTradeMin;
    }

    public void setInsideTradeMin(BigDecimal insideTradeMin) {
        this.insideTradeMin = insideTradeMin;
    }

    public Integer getCash() {
        return cash;
    }

    public void setCash(Integer cash) {
        this.cash = cash;
    }

    public Integer getRecharge() {
        return recharge;
    }

    public void setRecharge(Integer recharge) {
        this.recharge = recharge;
    }

    public Integer getOrderbys() {
        return orderbys;
    }

    public void setOrderbys(Integer orderbys) {
        this.orderbys = orderbys;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRechargeAddress() {
        return rechargeAddress;
    }

    public void setRechargeAddress(String rechargeAddress) {
        this.rechargeAddress = rechargeAddress;
    }

    public Integer getCashFeeCurrencyId() {
        return cashFeeCurrencyId;
    }

    public void setCashFeeCurrencyId(Integer cashFeeCurrencyId) {
        this.cashFeeCurrencyId = cashFeeCurrencyId;
    }

    public Integer getConvertFeeCurrencyId() {
        return convertFeeCurrencyId;
    }

    public void setConvertFeeCurrencyId(Integer convertFeeCurrencyId) {
        this.convertFeeCurrencyId = convertFeeCurrencyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNodeurl() {
        return nodeurl;
    }

    public void setNodeurl(String nodeurl) {
        this.nodeurl = nodeurl;
    }

    public Integer getIsintertr() {
        return isintertr;
    }

    public void setIsintertr(Integer isintertr) {
        this.isintertr = isintertr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public BigDecimal getDollar() {
        return dollar;
    }

    public void setDollar(BigDecimal dollar) {
        this.dollar = dollar;
    }

    public BigDecimal getUsdtRate() {
        return usdtRate;
    }

    public void setUsdtRate(BigDecimal usdtRate) {
        this.usdtRate = usdtRate;
    }

    @Override
    public String toString() {
        return "TCurrencyManage{" +
        ", id=" + id +
        ", currencyId=" + currencyId +
        ", coinname=" + coinname +
        ", icon=" + icon +
        ", cashFee=" + cashFee +
        ", cashfeeStatus=" + cashfeeStatus +
        ", cashfeeMin=" + cashfeeMin +
        ", insideTradeFee=" + insideTradeFee +
        ", insideTradeStatus=" + insideTradeStatus +
        ", insideTradeMin=" + insideTradeMin +
        ", cash=" + cash +
        ", recharge=" + recharge +
        ", orderbys=" + orderbys +
        ", status=" + status +
        ", rechargeAddress=" + rechargeAddress +
        ", cashFeeCurrencyId=" + cashFeeCurrencyId +
        ", convertFeeCurrencyId=" + convertFeeCurrencyId +
        ", username=" + username +
        ", password=" + password +
        ", nodeurl=" + nodeurl +
        ", isintertr=" + isintertr +
        ", createTime=" + createTime +
        ", creator=" + creator +
        ", updateTime=" + updateTime +
        ", updator=" + updator +
        "}";
    }

    public CurrencyInfoRes currencyInfoRes(){
        CurrencyInfoRes res = new CurrencyInfoRes();
        res.setCurrencyCode(coinname);
        res.setCurrencyName(coinname);
        res.setId(currencyId);
        res.setThumb(icon);
        res.setDollar(dollar);
        return res;
    }
}
