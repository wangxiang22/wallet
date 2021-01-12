package com.xinlian.common.scedule;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * <p>
 * 定时任务
 * </p>
 * <pre> Created: 2020/02/14 13:50  </pre>
 *
 * @author caimingshi
 * @version 1.0
 * @since JDK 1.8
 */
public abstract class AbstractSchedule implements SchedulingConfigurer {


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(
                this::schedule,
                //2.设置执行周期(Trigger)
                triggerContext ->
                        new CronTrigger(getCron())
                                .nextExecutionTime(triggerContext)
        );

    }

    /**
     * 获取cron表达式
     * @return
     */
    protected abstract String getCron();

    /**
     * 定时处理
     */
    private final void schedule() {
        doSchedule();
    }

    /**
     * 具体执行的定时任务
     */
    public abstract void doSchedule();

}
