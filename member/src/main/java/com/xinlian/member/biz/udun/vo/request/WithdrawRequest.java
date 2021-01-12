package com.xinlian.member.biz.udun.vo.request;

import lombok.Data;

@Data
public class WithdrawRequest {
    /**
     * 提币地址
     */
    private String address;
    /**
     * 提币金额
     */
    private String amount;
    /**
     * 商户号
     */
    private String merchantId;
    /**
     * 主币种编号
     */
    private String mainCoinType;
    /**
     * 子币种编号
     */
    private String coinType;
    /**
     * 手续费
     */
    private String fee;
    /**
     * 业务id
     */
    private String  businessId;

    private String callUrl;

    private String memo;
}
