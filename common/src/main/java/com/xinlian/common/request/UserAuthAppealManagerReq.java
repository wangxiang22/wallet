package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserAuthAppealManagerReq extends PageReq{
    private Long uid;//用户id
    private Integer appealStatus;//申诉审核状态 - 1：待审核，2：已拒绝，3：已更正
}
