package com.xinlian.common.request;

import lombok.Data;

@Data
public class TradeInfoReq {
    private Long uid;
    private Long coinId;
    private String type;
    private Integer page;
    private Integer end;
}
