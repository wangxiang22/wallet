package com.xinlian.rabbitMq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *  mq配置
 * </p>
 *
 * @author cms
 * @since 2020-04-13
 */
@Configuration
@Data
public class BaseMqConfig {

    @Value("${rabbit.host}")
    private String rabbitHost;
    @Value("${rabbit.port}")
    private int rabbitPort;
    @Value("${rabbit.username}")
    private String rabbitUsername;
    @Value("${rabbit.password}")
    private String rabbitPassword;

}
