package com.xinlian.biz.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCurrencyStateReq {
    private Long currencyId;//币种id
    private Long uid;//
    private Long exchangeId;//交易所id
    private BigDecimal amount;//冲提金额
    private Integer type;//1冲 交易所转入钱包 2提 钱包转入到交易所
    private String code;//短信验证码
    private String coinName;
    private String phone;
    private String email;
}
