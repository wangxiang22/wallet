package com.xinlian.common.request;

import lombok.Data;

@Data
public class SmartContractHistoryBillPageReq extends PageNumSizeReq {
    /**
     * 开始账期
     */
    private String startBillDate;
    /**
     * 结束账期
     */
    private String endBillDate;
    /**
     * 账单状态 1 正确  2 异常
     */
    private Integer billStatus;
}
