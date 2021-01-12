package com.xinlian.member.biz.smsvendor.aliyun;

import com.xinlian.member.biz.smsvendor.base.BaseSmsConfig;
import lombok.Data;

/**
 * @author Song
 * @date 2020-07-08 16:00
 * @description 阿里云国内短息配置
 */
@Data
public class AliyunInlandSmsConfig extends BaseSmsConfig {

    //keyId
    private String inlandAccessKeyId ;
    //secret
    private String inlandAccessKeySecret;
    //签名
    private String inlandSignName  ;
    //国内短信模板
    private String inlandTemplateCode ;
    //国内注册短信模板
    private String inlandRegisterTemplate;


}
