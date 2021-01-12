package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum PledgeApplyEnum {
    //质押申请状态 - 1：待审核，2：已拒绝，3：已通过
    PLEDGE_PENDING(1,"待审核"),
    PLEDGE_REJECTED(2,"已拒绝"),
    PLEDGE_PASS(3,"已通过"),
    ;

    /**
     * 质押申请状态码
     */
    private Integer pledgeCode;
    /**
     * 质押申请状态名称
     */
    private String pledgeValue;

    PledgeApplyEnum(Integer pledgeCode,String pledgeValue){
        this.pledgeCode=pledgeCode;
        this.pledgeValue=pledgeValue;
    }
}
