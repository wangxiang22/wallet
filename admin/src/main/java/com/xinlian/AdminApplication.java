package com.xinlian;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;

//注册服务
//@EnableDiscoveryClient
//feign 远程调用
//@EnableFeignClients
//允许断路器
//@EnableCircuitBreaker
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@MapperScan("com.xinlian.biz.dao")
@EnableAsync
public class AdminApplication extends WebMvcConfigurerAdapter {
    protected final static Logger logger = LoggerFactory.getLogger(AdminApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        logger.info("AdminApplication is success!");
    }




}

