package com.xinlian.common.request;

import lombok.Data;

@Data
public class CheckPwdReq {
    private String payPassword;
    private Long uid;

}
