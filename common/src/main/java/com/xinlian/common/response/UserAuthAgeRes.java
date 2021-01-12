package com.xinlian.common.response;

import lombok.Data;

@Data
public class UserAuthAgeRes {
    /**
     * 实名认证最小年龄限制
     */
    private String authMinAge;
    /**
     * 实名认证最大年龄限制
     */
    private String authMaxAge;

    public UserAuthAgeBooleanRes userAuthAgeBooleanRes() {
        UserAuthAgeBooleanRes userAuthAgeBooleanRes = new UserAuthAgeBooleanRes();
        userAuthAgeBooleanRes.setAuthMinAge(authMinAge);
        userAuthAgeBooleanRes.setAuthMaxAge(authMaxAge);
        return userAuthAgeBooleanRes;
    }
}
