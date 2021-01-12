package com.xinlian.common.request;

import lombok.Data;

@Data
public class OrderInfoRealTimePageReq extends PageNumSizeReq {
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 开始搜索时间
     */
    private Long startSearchTime;
    /**
     * 结束搜索时间
     */
    private Long endSearchTime;
}
