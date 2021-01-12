package com.xinlian.admin.server.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminRechargeListVo {
    private static final long serialVersionUID = 1L;

    private Long uid;
    private String userName;
    private String currencyCode;
    private BigDecimal tradeCurrencyNum;
    private String tradeAddress;
    private Integer tradeStatus;
    private String tradeStatusName;
    //交易类型
    private String des;
    //处理时间
    private String disposeCheckTime;
    //备注
    private String remark;
    //节点名称
    private String serverNodeName;
}
