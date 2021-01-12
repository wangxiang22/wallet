package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserAuthAppealReq {
    private Long uid;//用户id
    private Long nodeId;//节点id
    private String appealRealName;//申诉姓名
}
