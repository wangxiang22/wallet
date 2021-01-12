package com.xinlian.common.request;

public class UserInfoManagerReq extends PageReq {
    private Long uid;//用户id
    private String userName;//用户名
    private String mobile;//手机号码
    private String email;//邮箱
    private String realName;//真实姓名
    private String authSn;//身份证号码
    private String invitationCode;//邀请码
    private Integer levelStatus;//用户级别 - 0：冻结，1：普通用户
    private Long serverNodeId;//用户节点id
    private Integer startIndex;//分页查询开始索引

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAuthSn() {
        return authSn;
    }

    public void setAuthSn(String authSn) {
        this.authSn = authSn;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Integer getLevelStatus() {
        return levelStatus;
    }

    public void setLevelStatus(Integer levelStatus) {
        this.levelStatus = levelStatus;
    }

    public Long getServerNodeId() {
        return serverNodeId;
    }

    public void setServerNodeId(Long serverNodeId) {
        this.serverNodeId = serverNodeId;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
}
