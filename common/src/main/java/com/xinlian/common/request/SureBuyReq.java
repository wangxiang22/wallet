package com.xinlian.common.request;

import lombok.Data;

@Data
public class SureBuyReq {
    private Long uid;
    private String address;
    private String orderId;
}
