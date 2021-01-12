package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * com.xinlian.common.response
 *
 * @author by Song
 * @date 2020/2/18 20:37
 */
@Data
public class SmartContractHisBillResponse {

    private BigDecimal usdtPrice ;

    private String dateStr;
}
