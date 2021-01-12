package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoManagerRes {
    private Long uid;//用户id
    private String userName;//用户名
    private String mobile;//手机号码
    private String email;//邮箱
    private String headPortraitUrl;//头像url
    private String serverNodeName;//节点名称
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;//创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;//更新时间（作为冻结解冻时间字段）
    private String invitationCode;//邀请码
    private Integer levelStatus;//用户级别 - 0：冻结，1：普通用户
    private String freezeReson;//冻结原因
    private Integer oremState;//矿机激活状态 - 0：未激活，1：已激活
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date activeTime;//矿机激活时间
    private String realName;//真实姓名
    private String authSn;//身份证号
    private int chainOwner;//是否是链权人 - 0：不是链权人，1：是链权人
    private Long invitationNum;//已邀请人数
}
