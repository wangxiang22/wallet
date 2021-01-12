package com.xinlian.common.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RocketToWalletReq {

    private String rocketPhone;
    private Long rocketUid;
    private Long catUid;
    private BigDecimal amount;
    private String coinName;

}
