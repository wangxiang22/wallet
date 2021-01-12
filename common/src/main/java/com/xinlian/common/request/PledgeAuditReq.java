package com.xinlian.common.request;

import lombok.Data;

@Data
public class PledgeAuditReq {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 质押申请状态 - 1：待审核，2：已拒绝，3：已通过
     */
    private Integer pledgeStatus;
    /**
     * 拒绝理由
     */
    private String failReason;
}
