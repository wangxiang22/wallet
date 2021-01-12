package com.xinlian.member.biz.udun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 优盾配置相关信息
 */
@Data
@PropertySource(ignoreResourceNotFound = true,value = {"classpath:udun.properties"})
@Component
@ConfigurationProperties(prefix = "udun")
public class UdunConfig {

    //提币接口
    private String withdraw;
    //代付接口
    private String proxyPay;
    //生成地址
    private String createAddress;
    //基础地址
    private String gatewayHost;
    //回调基础地址
    private String callbackRoot;
    //回调地址
    private String callbackUri;
    //商户
    private String merchantId;
    //商户key
    private String merchantKey;
    //请求优盾主币地址
    private String mainCoinType;
    //代币合约地址
    private String tokenContractAddress;

    private String timingTaskFlag;
}
