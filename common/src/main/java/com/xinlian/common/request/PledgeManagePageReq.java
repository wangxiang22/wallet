package com.xinlian.common.request;

import lombok.Data;

@Data
public class PledgeManagePageReq extends PageNumSizeReq {
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 节点id
     */
    private Long nodeId;
    /**
     * 质押申请状态 - 1：待审核，2：已拒绝，3：已通过
     */
    private Integer pledgeStatus;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 交易流水描述
     */
    private String des;

    public void setPledgeStatus(Integer pledgeStatus) {
        //交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
        switch (pledgeStatus) {
            case 1:
                this.pledgeStatus = 1;
                break;
            case 2:
                this.pledgeStatus = 4;
                break;
            case 3:
                this.pledgeStatus = 7;
                break;
        }
    }
}
