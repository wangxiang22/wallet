package com.xinlian.common.request;

import lombok.Data;

@Data
public class QuerySpotInfoReq extends BaseReq {
    private String orderId;//订单号
    private Long uid;//uid
    private String username;
    private String phone;
    private Integer type;//交易方向0买1卖
    private Long startTime;
    private Long endTime;
    private Integer state;//0.等待交易 1.已过期 2.已成交
}
