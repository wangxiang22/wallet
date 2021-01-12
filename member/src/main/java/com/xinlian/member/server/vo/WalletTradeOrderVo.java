package com.xinlian.member.server.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WalletTradeOrderVo {
    private static final long serialVersionUID = 1L;

    private Long uid;
    private Long tradeId;
    private Long currencyId;
    private String currencyCode;
    private BigDecimal tradeCurrencyNum;
    private Integer tradeType;
    private Integer tradeStatus;
    private String tradeStatusName;
    private String des;
    private Date createTime;
    private BigDecimal balanceNum;
    private Long walletId;

}
