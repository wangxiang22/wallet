package com.xinlian.member.biz.smsvendor.yunpian;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-11 12:35
 * @description 云片国际短信配置
 */
@Data
public class YunPianAbroadSmsConfig {

    private String apiKey ;

    private String smsTemplate;

    private String registerSmsTemplate;
}
