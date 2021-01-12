package com.xinlian.member.biz.chuanglan.util;

import lombok.Data;

/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:02
 */
@Data
public class ChuangLanAbroadSmsConfig {
    //国外账号
    private String abroadAccount ;
    //国外密码
    private String abroadPassword;
    //国外短信链接
    private String abroadSmsUrl  ;
    //国外短信签名
    private String abroadSmsSign ;
    //国外短信模板
    private String abroadSmsTemplate ;
    //国外注册短信模板
    private String abroadSmsRegisterTemplate;

}
