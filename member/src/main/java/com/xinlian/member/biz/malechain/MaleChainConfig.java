package com.xinlian.member.biz.malechain;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@PropertySource(ignoreResourceNotFound = true,value = {"classpath:maleChain.properties"})
@Component
@ConfigurationProperties(prefix = "male.chain")
public class MaleChainConfig {

    //提币接口
    private String withdraw;
    //生成地址
    private String createAddress;
    //基础地址
    private String gatewayHost;
    //回调基础地址
    private String callbackRoot;
    //回调地址
    private String callbackUri;
    //查询结果
    private String searchResultUri;
    //查询有余额地址
    private String haveBalanceAddress;
    //请求优盾主币地址
    private String mainCoinType;

    private String timingTaskFlag;

    private String keyUrl;

    private String publicKey;

    private String privateKey;

}
