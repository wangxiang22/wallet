package com.xinlian.member.biz.smsvendor.sendcloud;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-13 10:49
 * @description
 */
@Data
public class SendCloudBaseSmsConfig {

    //smsUser
    private String smsUser;
    //smskey
    private String smsKey;
    //请求地址
    private String requestSmsUrl;

    //业务类型，"0"代表国内短信，"2"代表国际短信，默认国内短信
    private String msgType ;

    //模板id
    private String templateIdStr ;

    //注册模板id
    private String registerTemplateIdStr ;
}
