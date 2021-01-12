package com.xinlian.common.request;

import lombok.Data;

@Data
public class UserListReq extends PageReq{
    //0冻结 1普通用户 2会员
    private Integer levelStatus;
    //节点
    private Long serverNodeId;
    //1用户名 2uid 3手机号 4邮箱 5真实姓名 6邀请码
    private Integer paramType;
    //请求参数值
    private String paramValue;

}
