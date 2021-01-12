package com.xinlian.common.response;

import lombok.Data;

@Data
public class InviteUserRes {
    private Long uid;
    private String userName;
    private String phone;
    private String nodeName;
    private String email;
    private String des;
}
