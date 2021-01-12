package com.xinlian.member.biz.smsvendor.base;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-08 11:58
 * @description
 */
@Data
public class SendSmsModel {

    private String phone; //不包含地区区号

    private Integer countryCode;//区域

    private String code;//sms code

    private String redisSmsKey; //save sms code key

    private int reqType; //sms use type
}
