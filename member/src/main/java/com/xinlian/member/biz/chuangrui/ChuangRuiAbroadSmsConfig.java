package com.xinlian.member.biz.chuangrui;

import lombok.Data;

/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:02
 */
@Data
public class ChuangRuiAbroadSmsConfig {
    //国际开发key
    private String abroadAccesskey ;
    //国际开发秘钥
    private String abroadAccessSecret ;
    //国际短信链接
    private String abroadSmsUrl  ;
    //国际短信签名
    private String abroadSmsSign ;
    //国际短信模板
    private String abroadSmsTemplate ;
    //国际注册短信模板
    private String abroadSmsRegisterTemplate ;


}
