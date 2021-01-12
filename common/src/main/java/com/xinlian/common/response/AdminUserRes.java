package com.xinlian.common.response;

import lombok.Data;

@Data
public class AdminUserRes {
    private String username;
    private String realName;
    private String roleCode;
    private Long userId;
    private Long createTime;
    private String token;
}
