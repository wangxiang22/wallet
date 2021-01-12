package com.xinlian.common.request;

import lombok.Data;

@Data
public class UpdateUserReq {
    private Long uid;
    private String name;
    private String avatar;
    //极光账号id
    private String jid;
    //极光账号类型 1 安卓  2 IOS
    private Integer type;
}
