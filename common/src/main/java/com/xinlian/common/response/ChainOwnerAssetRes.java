package com.xinlian.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainOwnerAssetRes {
    private BigDecimal catValue;//链权人锁仓cat实时价值
    private BigDecimal catAmount;//链权人锁仓cat数量
    private Integer signStatus;//用户签约链权人状态 - 0：未签约，1：已签约
    private String url;//链权人证书地址
}
