package com.xinlian.common.response;

import lombok.Data;

@Data
public class WalletFindUserRes {
    private Long uid;//用户id
    private String userName;//用户名
    private String mobile;//手机号码
    private String headPortraitUrl;//头像url
    private Integer levelStatus;//用户级别 - 0：冻结，1：普通用户
    private Integer oremState;//矿机激活状态 - 0：未激活，1：已激活
    private String currencyAddress;//钱包地址
    private String udunCurrencyAddress;//旧钱包地址
}
