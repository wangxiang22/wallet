package com.xinlian;


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
@SpringBootApplication
@EnableAsync
@MapperScan("com.xinlian.biz.dao")
@ComponentScan(basePackages = {"com.xinlian.netty", "com.xinlian.biz.utils"})
@EnableScheduling
@Slf4j
public class NettyApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NettyApplication.class);
    }

    public static void main(String[] args) {
        log.info("NettyApplication is success!");
        SpringApplication.run(NettyApplication.class, args);

    }
}

