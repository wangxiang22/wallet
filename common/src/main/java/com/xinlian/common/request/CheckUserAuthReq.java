package com.xinlian.common.request;

import lombok.Data;

@Data
public class CheckUserAuthReq extends PageReq{
    private String realName;
    private Long uid;
    private String authNo6;
    private String userName;
    private String payPwd;
    private String authScsfz;//手持身份证
    private Integer state;
}
