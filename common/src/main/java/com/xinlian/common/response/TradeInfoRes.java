package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeInfoRes {
    private Long id;
    private Long currencyId;
    private String currencyCode;
    private String tradeAddress;
    private BigDecimal tradeCurrencyNum;
    private Integer tradeStatus;
    //交易状态中文名称
    private String tradeStatusName;
    private Date createTime;
    private String tel;
    private String name;
    private String failReason;
    private Integer tradeType;
    private String des;
    private String counterPartyMobile;
    private String counterPartyUserName;
}
