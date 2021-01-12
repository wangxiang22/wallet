package com.xinlian.common.request;

import lombok.Data;

@Data
public class OrderStateReq {
    private Integer type;//1.买 2. 卖
    private Long uid;
}
