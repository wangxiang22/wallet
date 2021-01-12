package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum PledgeUserApplyEnum {
    //挖矿质押状态 - 1：未质押，2：待审核质押，3：已质押
    NOT_PLEDGE(1,"未质押"),
    PENDING_APPROVAL_PLEDGE(2,"待审核质押"),
    HAVE_PLEDGE(3,"已质押"),
    ;

    /**
     * 质押申请状态码
     */
    private Integer pledgeCode;
    /**
     * 质押申请状态名称
     */
    private String pledgeValue;

    PledgeUserApplyEnum(Integer pledgeCode, String pledgeValue){
        this.pledgeCode=pledgeCode;
        this.pledgeValue=pledgeValue;
    }
}
