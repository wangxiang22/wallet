package com.xinlian.common.request;

import com.xinlian.common.result.BizException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class WithdrawTradeSuccessTriggerReq implements ICheckParam {
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;


    @Override
    public void checkParam() {
        if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
            throw new BizException("时间参数不能为空!");
        }
    }
}
