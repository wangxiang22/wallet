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
 * 账户大额变动流水记录表
 * </p>
 *
 * @author lt
 * @since 2020-08-13
 */
@Data
@TableName("t_withdraw_trade_success_log")
public class TWithdrawTradeSuccessLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;
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
     * 收款/付款方uid
     */
    @TableField("counter_party_uid")
    private Long counterPartyUid;
    /**
     * 交易地址
     */
    @TableField("trade_address")
    private String tradeAddress;
    /**
     * 交易币种数量
     */
    @TableField("trade_currency_num")
    private BigDecimal tradeCurrencyNum;
    /**
     * 描述
     */
    @TableField("des")
    private String des;
    /**
     * 区块链交易哈希
     */
    @TableField("tx_id")
    private String txId;
    /**
     * 交易时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("create_time")
    private Date createTime;
    /**
     * 唯一标识码
     */
    @TableField("unique_code")
    private String uniqueCode;


}
