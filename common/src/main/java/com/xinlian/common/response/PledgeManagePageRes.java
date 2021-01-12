package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PledgeManagePageRes {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 节点id
     */
    private Long nodeId;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 质押金额
     */
    private BigDecimal pledgeNum;
    /**
     * 质押申请状态 - 1：待审核，2：已拒绝，3：已通过
     */
    private String pledgeStatusName;
    /**
     * 提交审核的时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 备注：成功的时候显示审核成功，失败的时候显示审核失败
     */
    private String remark;
    /**
     * 拒绝理由
     */
    private String failReason;
}
