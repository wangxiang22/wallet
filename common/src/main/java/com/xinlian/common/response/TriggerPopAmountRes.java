package com.xinlian.common.response;

import lombok.Data;

@Data
public class TriggerPopAmountRes {
    /**
     * USDT触发弹窗的金额
     */
    private String triggerPopUsdt;
    /**
     * CAT触发弹窗的金额
     */
    private String triggerPopCat;
    /**
     * CAG触发弹窗的金额
     */
    private String triggerPopCag;
}
