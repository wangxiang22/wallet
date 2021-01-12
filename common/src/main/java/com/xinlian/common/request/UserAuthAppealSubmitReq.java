package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserAuthAppealSubmitReq {
    private Long uid;//用户id
    private String appealRealName;//申诉姓名
    private Integer appealStatus;//申诉审核状态 - 1：待审核，2：已拒绝，3：已更正
    private String note;//处理原因
}
