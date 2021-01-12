package com.xinlian.common.response;

import lombok.Data;

/**
 * 实名认证时国内证件的判断
 */
@Data
public class UserAuthAgeBooleanRes {
    /**
     * 是否符合年龄限制
     */
    private boolean flag;
    /**
     * 实名认证最小年龄限制
     */
    private String authMinAge;
    /**
     * 实名认证最大年龄限制
     */
    private String authMaxAge;
}
