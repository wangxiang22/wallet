package com.xinlian.member.biz.smsvendor.aliyun;

import com.xinlian.member.biz.smsvendor.base.BaseSmsConfig;
import lombok.Data;

/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:02
 * @description 阿里云国外短信配置
 */
@Data
public class AliyunAbroadSmsConfig extends BaseSmsConfig {
    //国外keyId
    private String abroadAccessKeyId ;
    //国外secret
    private String abroadAccessKeySecret;
    //国外短信签名
    private String abroadSmsSign ;
    //国外短信模板
    private String abroadTemplateCode ;
    //国外注册短信模板
    private String abroadRegisterTemplate;

}
