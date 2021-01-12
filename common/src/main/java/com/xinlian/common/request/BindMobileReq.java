package com.xinlian.common.request;

import lombok.Data;

@Data
public class BindMobileReq {
    private Long uid;
    private Long nodeId;
    private String mobile;
    private String code;
    //区域
    private Integer countryCode;
    private int type;
}
