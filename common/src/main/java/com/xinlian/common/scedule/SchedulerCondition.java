package com.xinlian.common.scedule;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * <p>
 * 定时调度配置
 * </p>
 * <pre> Created: 2020/01/14 13:30  </pre>
 *
 * @author caimingshi
 * @version 1.0
 * @since JDK 1.8
 */
public class SchedulerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return true;
    }

}