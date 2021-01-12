package com.xinlian.common.request;

import lombok.Data;

@Data
public class AddWithdrawAddressReq {
    private Long uid;
    private String currencyId;
    private String currencyAddress;
    private String addressName;
}
