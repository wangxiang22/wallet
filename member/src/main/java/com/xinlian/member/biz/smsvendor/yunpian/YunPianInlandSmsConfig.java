package com.xinlian.member.biz.smsvendor.yunpian;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-11 12:34
 * @description 云片国内短信配置
 */
@Data
public class YunPianInlandSmsConfig {

    private String apiKey ;

    private String smsTemplate;

    private String registerSmsTemplate;

}
