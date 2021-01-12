package com.xinlian.common.scedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <p>
 * 定时调度配置
 * </p>
 * <pre> Created: 2020/02/14 13:56  </pre>
 *
 * @author caimingshi
 * @version 1.0
 * @since JDK 1.8
 */
@Configuration
public class ScheduleConfig {
    @Bean
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(5);
    }
}
