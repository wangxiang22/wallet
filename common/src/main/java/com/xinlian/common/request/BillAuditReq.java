package com.xinlian.common.request;

import lombok.Data;

@Data
public class BillAuditReq {
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
}
