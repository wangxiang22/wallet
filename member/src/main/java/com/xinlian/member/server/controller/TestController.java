package com.xinlian.member.server.controller;

import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    @Autowired
    private ChuangLanSmsService chuangLanSmsService;

    @PassToken
    @RequestMapping("/test")
    public void test(HttpServletRequest httpServletRequest){

        String ipAddr = SystemUtils.getIpAddress(httpServletRequest);
        System.out.println(ipAddr);
    }



    @PassToken
    @GetMapping("/testSendSms")
    public String testSendSms(@RequestParam String phone,@RequestParam String auth){
        if(!"auth!202009".equals(auth)){
            return "授权不对";
        }
        chuangLanSmsService.sendRegisterCodeCh(phone,"283121");
        return "ok";
    }
}
