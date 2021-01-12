package com.xinlian.admin.server.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTradeOrderVo {
    private static final long serialVersionUID = 1L;

    private Long uid;
    private String userName;
    private Long tradeId;
    private Long currencyId;
    private String currencyCode;
    private BigDecimal tradeCurrencyNum;
    private String tradeAddress;
    private Integer tradeType;
    private Integer tradeStatus;
    private String tradeStatusName;
    private String des;
    private String createTime;
    private String disposeCheckTime;
    private BigDecimal balanceNum;
    private Long walletId;
    private String txId;
    //失败原因
    private String failReason;
    private Long serverNodeId;
    //节点名称
    private String serverNodeName;
}
