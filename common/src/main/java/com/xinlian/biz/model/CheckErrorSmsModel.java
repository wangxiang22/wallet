package com.xinlian.biz.model;

import lombok.Data;

/**
 * @author Song
 * @date 2020-06-15 17:40
 * @description
 */
@Data
public class CheckErrorSmsModel {

    private String redisSmsCode;

    private String reqCode;

    private String smsRedisKey;

    private String phone;

    private int countryCode;
}
