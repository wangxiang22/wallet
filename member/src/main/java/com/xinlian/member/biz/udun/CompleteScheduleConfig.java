package com.xinlian.member.biz.udun;

import com.xinlian.member.biz.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.Resource;

@Configuration
@EnableScheduling
@MapperScan("com.xinlian.member.biz.udun")
@Slf4j
public abstract class CompleteScheduleConfig implements SchedulingConfigurer {
    private String defaultExpression = "0 0/30 * * * ?";
    @Mapper
    public interface CronMapper {
        @Select("select option_value from admin_options where option_name = 'req_udun_task' limit 1")
        String getReqUdunCron();

        @Select("select option_value from admin_options where option_name = 'create_address_task' limit 1")
        String getCreateAddressCron();

        @Select("select option_value from admin_options where option_name = 'trc_usdt_address_task' limit 1")
        String getTrcUsdtAddressCron();
    }

    @Resource
    public CronMapper cronMapper;

    @Autowired
    public RedisClient redisClient;
    private String taskKey = "UDUN_CREATE_ADDRESS_REDIS_KEY";
    /**
     * 执行定时任务.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                ()->{
                    if(null==redisClient.get(taskKey)) {
                        this.doSubClassTask();
                    }
                },
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //2.1 从数据库获取执行周期
                    String cron = getCronMapper();
                    //2.2 合法性校验.
                    if (StringUtils.isEmpty(cron)) {
                        // Omitted Code ..
                        redisClient.set(taskKey,"coron暂时没有值");
                        //获取不到 执行静默方法
                        return new CronTrigger(defaultExpression).nextExecutionTime(triggerContext);
                    }
                    redisClient.deleteByKey(taskKey);
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }


    public abstract void doSubClassTask();

    public abstract String getCronMapper();
}
