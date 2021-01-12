package com.xinlian.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinlian.common.response.PledgeManagePageRes;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PledgeManagePageDto {
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
     * 交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
     */
    private Integer tradeStatus;
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


    public PledgeManagePageRes pledgeManagePageRes() {
        PledgeManagePageRes pledgeManagePageRes = new PledgeManagePageRes();
        pledgeManagePageRes.setUid(uid);
        pledgeManagePageRes.setUserName(userName);
        pledgeManagePageRes.setNodeId(nodeId);
        pledgeManagePageRes.setNodeName(nodeName);
        pledgeManagePageRes.setPledgeNum(pledgeNum);
        pledgeManagePageRes.setCreateTime(createTime);
        pledgeManagePageRes.setRemark(remark);
        pledgeManagePageRes.setFailReason(failReason);
        //1：待审核，2：已拒绝，3：已通过
        switch (tradeStatus) {
            case 1:
                pledgeManagePageRes.setPledgeStatusName("待审核");
                break;
            case 4:
                pledgeManagePageRes.setPledgeStatusName("已拒绝");
                break;
            case 7:
                pledgeManagePageRes.setPledgeStatusName("已通过");
                break;
        }
        return pledgeManagePageRes;
    }
}
