package com.xinlian.common.request;

import org.apache.commons.lang3.StringUtils;

public class UpdatePwdReq implements ICheckParam {
    private Long uid;
    private String oldPwd;
    private String password;
    private String password2;
    private String code;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void checkParam() {
        if(StringUtils.isEmpty(oldPwd)){
            throwException();
        }
        if(StringUtils.isEmpty(password)){
            throwException();
        }
        if(StringUtils.isEmpty(password2)){
            throwException();
        }
    }

    public void checkForgetPayPwdParam(){
        if(StringUtils.isEmpty(password)){
            throwException();
        }
        if(StringUtils.isEmpty(password2)){
            throwException();
        }
        if(StringUtils.isEmpty(code)){
            throwException();
        }
    }

}
