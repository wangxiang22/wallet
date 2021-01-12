package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class AllCurrencyRes {
    private Long uid;//用户id
    private Long currencyId;
    @TableField("balance_num")
    private BigDecimal balanceNum;//余额
    private String coinname;
    private String icon;
    @TableField("cash_fee")
    private BigDecimal cashFee;//提现手续费
    @TableField("cashfee_status")
    private Integer cashfeeStatus;//提现手续费类型: 1、比例 2、固定值
    @TableField("cashfee_min")
    private BigDecimal cashfeeMin;//最低提现手续费
    private Integer cash;//是否允许提现 1-是 2-否
    private Integer recharge;//是否允许充值：1:是；2:否
    @TableField(exist = false)
    private BigDecimal dollariteam;//用户所拥有的该币种总价值
    private BigDecimal dollar;//汇率
    private BigDecimal amount;//用户锁仓金额（新大陆usdt需要换算成cat）
    private Integer amountStatus;//是否展示锁仓金额 - 0：不展示，1：展示
}
