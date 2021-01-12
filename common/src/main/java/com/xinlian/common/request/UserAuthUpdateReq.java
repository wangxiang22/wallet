package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserAuthUpdateReq {
    private String realName;
    private String authSn;
    private String tel;
    private String note;
    private Long uid;
    private String sfzzm;//身份证正面
    private String sfzfm;//身份证反面
    private String scsfz;//手持身份证
}
