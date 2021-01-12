package com.xinlian.member.biz.chuanglan.util;

import lombok.Data;

/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:02
 */
@Data
public class ChuangLanInlandSmsConfig {
    //国内账号
    private String inlandAccount ;
    //国内密码
    private String inlandPassword;
    //国内短信链接
    private String inlandSmsUrl  ;
    //国内短信签名
    private String inlandSmsSign ;
    //国内短信模板
    private String inlandSmsTemplate ;
    //国内注册短信模板
    private String inlandSmsRegisterTemplate;


}
