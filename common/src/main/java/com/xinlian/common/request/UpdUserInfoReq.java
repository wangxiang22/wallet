package com.xinlian.common.request;

import lombok.Data;

@Data
public class UpdUserInfoReq {
    private Long uid;
    private String email;
    private String username;
    //0冻结 1普通用户 2会员
    private Integer levelStatus;
    //电话
    private String phone;
    //昵称
    private String nickName;
    //邀请人
    private String parentName;
    //节点名称
    private Long nodeId;
    //真实名称
    private String realName;
    //身份证号
    private String idNo;
}
