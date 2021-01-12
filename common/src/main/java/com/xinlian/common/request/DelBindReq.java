package com.xinlian.common.request;

import lombok.Data;

@Data
public class DelBindReq {
    private String code;
    private String phone;
    private Long uid;
    private String email;
    private Long rocketUid;
}
