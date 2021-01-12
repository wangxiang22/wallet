package com.xinlian.common.request;

import com.xinlian.common.result.BizException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceBillOperationReq implements ICheckParam {
    /**
     * 币种名称
     */
    private String currencyName;
    /**
     * 账单分类名称
     */
    private String billName;
    /**
     * 对冲数量
     */
    private BigDecimal hedgeAmount;
    /**
     * 对冲时间（年-月-日格式）
     */
    private Date hedgeTime;
    /**
     * 备注
     */
    private String remarks;

    @Override
    public void checkParam() {
        if(StringUtils.isEmpty(currencyName)){
            throw new BizException("币种名称不能为空！");
        }
        if(StringUtils.isEmpty(billName)){
            throw new BizException("账单分类不能为空！");
        }
        if(hedgeAmount == null){
            throw new BizException("对冲数量不能为空！");
        }
        if(hedgeTime == null){
            throw new BizException("对冲时间不能为空！");
        }
        if(StringUtils.isEmpty(remarks)){
            throw new BizException("备注不能为空！");
        }
    }
}
