package com.xinlian.common.request;

import lombok.Data;

@Data
public class ExWalletWithdrawReq {
    private String code;
    private String tel;
    private Long uid;
}
