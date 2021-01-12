package com.xinlian.common.express;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里快递配置
 */
@ConfigurationProperties(prefix = "aliyun.express")
@EnableConfigurationProperties(value = {ExpressConfig.class})
@Configuration
@Data
public class ExpressConfig {

    private String appCode;

    private String appKey;

    private String appSecret;

    private String queryCompanyUrl;

    private String queryExpressUrl;

}
