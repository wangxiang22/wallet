package com.xinlian.biz.model;

import lombok.Data;
import java.math.BigDecimal;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/5 09:27
 */
@Data
public class MiddleModel {

    private Long uid;

    private BigDecimal tradeSum;

    private String currencyCode;

    private Long orderId;

    private Integer status;
}
