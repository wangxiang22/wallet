package com.xinlian.common.request;

import lombok.Data;

@Data
public class FindAllUserReq extends BaseReq{
    private Long uid;
    private String authSn;
    private String userName;
    private Integer nodeId;
    private Integer state;
}
