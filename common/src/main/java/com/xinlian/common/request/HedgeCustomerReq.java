package com.xinlian.common.request;

import lombok.Data;

@Data
public class HedgeCustomerReq {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 冻结客户资产类型：HedgeCustomerWalletEnum中有明确类型
     */
    private Integer hedgeCode;
}
