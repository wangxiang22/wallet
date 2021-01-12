package com.xinlian.common.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 质押扣款参数
 */
@Data
public class PledgeReq {
    //用户id
    private Long uid;
    //节点id
    private Long nodeId;
    //质押金额
    private BigDecimal pledgeAmount;
    //币种编码名称
    private String currencyCode;
    //用户支付密码
    private String payPassWord;
}
