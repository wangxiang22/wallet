package com.xinlian.common.response;

import lombok.Data;

import java.util.Date;

@Data
public class UserMessageRes {
    private Long uid;
    //节点名称
    private String serverNodeName;
    //用户名
    private String userName;
    //实名姓名
    private String authName;
    //激活时间
    private Date activeTime;
    //身份证号
    private String authSn;
    //节点logo图片地址
    private String logoUrl;
    //激活状态：0 ：没激活，1 ：已激活
    private String oreState;

}
