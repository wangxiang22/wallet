package com.xinlian;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
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
@EnableAsync
@MapperScan("com.xinlian.biz.dao")
@ComponentScan(basePackages = {"com.xinlian.member","com.xinlian.biz.utils","com.xinlian.common","com.xinlian.rabbitMq"})
@EnableScheduling
@Slf4j
public class MemberApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MemberApplication.class);
    }
    public static void main(String[] args) {
        log.info("MemberApplication is success!");
        SpringApplication.run(MemberApplication.class, args);

    }
}

