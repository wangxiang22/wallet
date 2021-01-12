package com.xinlian.common.request;

import lombok.Data;

@Data
public class BindMailReq {
    private Long uid;
    private String email;
    private String code;
}
