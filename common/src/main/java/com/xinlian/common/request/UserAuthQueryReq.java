package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserAuthQueryReq {
    private Long uid;
    private String username;
    private String realName;
    private Integer node;
    private Integer status;
    //身份证号码
    private String authSn;
    private Long startTime;
    private Long endTime;
    private Long pageNum;
    private Long pageNum2;

    private Integer from; //1:中国 2：外国
}
