package com.xinlian.member.biz.chuangrui;

import lombok.Data;

/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:02
 */
@Data
public class ChuangRuiInlandSmsConfig {
    //国内开发key
    private String accesskey ;
    //国内开发秘钥
    private String accessSecret ;
    //国内短信链接
    private String inlandSmsUrl  ;
    //国内短信签名
    private String inlandSmsSign ;
    //国内短信模板
    private String inlandSmsTemplate ;
    //国内注册短信模板
    private String inlandSmsRegisterTemplate ;


}
