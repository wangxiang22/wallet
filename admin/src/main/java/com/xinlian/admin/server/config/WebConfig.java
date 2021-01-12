package com.xinlian.admin.server.config;


import com.xinlian.admin.biz.myshiro.filter.JwtFilterMap;
import com.xinlian.admin.biz.myshiro.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public AuthenticationInterceptor interceptor(){
        AuthenticationInterceptor interceptor = new AuthenticationInterceptor();
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor()).addPathPatterns("/**");
    }

    @Bean
    public JwtFilterMap filterMap(){
        JwtFilterMap filterMap = new JwtFilterMap();
        filterMap.addRoleUrl("/swagger-resources", Arrays.asList("none"));
        filterMap.addRoleUrl("/webjars", Arrays.asList("none"));
        filterMap.addRoleUrl("/v2", Arrays.asList("none"));
        filterMap.addRoleUrl("/swagger-ui.html", Arrays.asList("none"));
        filterMap.addRoleUrl("/login/login", Arrays.asList("none"));
        filterMap.addRoleUrl("/login/login1421Login", Arrays.asList("none"));
        filterMap.addRoleUrl("/login/sendEmailCode",Arrays.asList("none"));
        filterMap.addRoleUrl("/upload/", Arrays.asList("none"));
        filterMap.addRoleUrl("/financeCheck/v1/updateAccountCheckData",Arrays.asList("none"));
        filterMap.addRoleUrl("/financeCheck/v1/summaryTopNodeDat",Arrays.asList("none"));
        filterMap.addRoleUrl("/smartContractHistoryBill/v1/compensate/billDate",Arrays.asList("none"));
        filterMap.addRoleUrl("/userAuthManager/batchAuditByBaiduRecognition", Arrays.asList("none"));//重新过滤国内通道已拒绝的实名信息
        filterMap.addRoleUrl("/userNextLevel/export",Arrays.asList("none"));

//        filterMap.addRoleUrl("/login/pwd", Arrays.asList("administrator","customerservice",""));
//        //系统用户管理
//        filterMap.addRoleUrl("/member/", Arrays.asList("administrator"));
//        filterMap.addRoleUrl("/user/", Arrays.asList("administrator"));

//        filterMap.addRoleUrl("/serverNodeManager/", Arrays.asList("administrator"));//wallet节点管理
//        filterMap.addRoleUrl("/adminOptions/", Arrays.asList("administrator"));//wallet配置项管理
//        filterMap.addRoleUrl("/userAuthManager/", Arrays.asList("administrator"));//用户实名管理
//        filterMap.addRoleUrl("/tVersion/", Arrays.asList("administrator"));//用户实名管理
//        filterMap.addRoleUrl("/tNewsArticle/", Arrays.asList("administrator"));//新闻管理
//        filterMap.addRoleUrl("/currencyManager/", Arrays.asList("administrator"));//新闻管理
//        filterMap.addRoleUrl("/walletTrade/", Arrays.asList("administrator"));//交易管理
//        filterMap.addRoleUrl("/index/v1/",Arrays.asList("administrator"));//首页数据接口
//        filterMap.addRoleUrl("/pushNoticeManager/", Arrays.asList("none"));//推送通知管理
//        filterMap.addRoleUrl("/chainOwnerManager/", Arrays.asList("none"));//链权人信息管理
//        filterMap.addRoleUrl("/userAuthAppealManager/", Arrays.asList("none"));//实名申诉管理
//        filterMap.addRoleUrl("/pledgeManage/", Arrays.asList("none"));//矿池质押审核
//        filterMap.addRoleUrl("/adminMessageContent/", Arrays.asList("none"));//消息内容管理
//        filterMap.addRoleUrl("/smartContractHistoryBill/", Arrays.asList("none"));//智能合约历史账单管理

//        filterMap.addRoleUrl("/user/", Arrays.asList("none"));//用户管理
//        filterMap.addRoleUrl("/test/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/homePage/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/operationLog/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/dictionaryConfiguration/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/docConfiguration/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/docManagement/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/message/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/newUser/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/role/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/master/", Arrays.asList("none"));
//        filterMap.addRoleUrl("/release/v1/upload/",Arrays.asList("none"));
        return filterMap;
    }
}
