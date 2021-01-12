package com.xinlian.common.response;


public class AuthenticationRes extends UserInfoRes {
    //证据号
    private String authSn;
    //真实姓名
    private String realName;
    //处理状态 1审核中2审核失败3审核成功
    private int status;
    //身份证反面
    private String auth_sfzfm;
    //身份证正面
    private String auth_sfzzm;
    //手持身份证
    private String auth_scsfz;

    public String getAuthSn() {
        return authSn;
    }

    public void setAuthSn(String authSn) {
        this.authSn = authSn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAuth_sfzfm() {
        return auth_sfzfm;
    }

    public void setAuth_sfzfm(String auth_sfzfm) {
        this.auth_sfzfm = auth_sfzfm;
    }

    public String getAuth_sfzzm() {
        return auth_sfzzm;
    }

    public void setAuth_sfzzm(String auth_sfzzm) {
        this.auth_sfzzm = auth_sfzzm;
    }

    public String getAuth_scsfz() {
        return auth_scsfz;
    }

    public void setAuth_scsfz(String auth_scsfz) {
        this.auth_scsfz = auth_scsfz;
    }
}
