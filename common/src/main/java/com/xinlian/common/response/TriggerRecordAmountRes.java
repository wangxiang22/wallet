package com.xinlian.common.response;

import lombok.Data;

@Data
public class TriggerRecordAmountRes {
    /**
     * USDT触发写入记录表的金额
     */
    private String triggerRecordUsdt;
    /**
     * CAT触发写入记录表的金额
     */
    private String triggerRecordCat;
    /**
     * CAG触发写入记录表的金额
     */
    private String triggerRecordCag;
}
