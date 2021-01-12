package com.xinlian.common.request;

import lombok.Data;

@Data
public class RefuseReq {
    private Long uid;
    private String refuseReason;
}
