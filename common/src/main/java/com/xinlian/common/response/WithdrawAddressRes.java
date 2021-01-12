package com.xinlian.common.response;

import lombok.Data;

@Data
public class WithdrawAddressRes {
    private Long id;
    private Long coinId;
    private String coinCode;
    private String coinName;
    private String address;
    private String addressName;
    private Long inputtime;
    //币种图片 地址
    private String thumb;
}
