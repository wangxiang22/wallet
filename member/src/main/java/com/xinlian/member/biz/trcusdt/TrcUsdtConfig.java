package com.xinlian.member.biz.trcusdt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 无名氏
 * @date 2020-08-18 15:39
 * @description
 */
@Data
@PropertySource(ignoreResourceNotFound = true,value = {"classpath:trcUsdt.properties"})
@Component
@ConfigurationProperties(prefix = "trc.usdt")
public class TrcUsdtConfig {

    private String timingTaskFlag;

    private String privateKey;
    //基础地址
    private String gatewayHost;
    //生成地址
    private String createAddress;
    //充值查询查询
    private String rechargeCompensateSearch;
    //提币
    private String trcWithdraw;


}
