package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.model.Address;
import com.xinlian.biz.model.TAddressPool;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.malechain.MaleChainConfig;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IAddressPoolService;
import com.xinlian.member.biz.service.MaleChainService;
import com.xinlian.member.biz.udun.CompleteScheduleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@Slf4j
public class MaleChainAddressHandler extends CompleteScheduleConfig {

    @Autowired
    private MaleChainService maleChainService;
    @Autowired
    private IAddressPoolService addressPoolService;
    @Autowired
    private MaleChainConfig maleChainConfig;
    @Autowired
    private RedisClient redisClient;


    //3.添加定时任务
    //或直接指定时间间隔，例如：5秒
    @Override
    public void doSubClassTask() {
        try {
            log.info(Thread.currentThread().getName() + " : 请求生成币种地址接口获取分布式锁success！");
            //某个整点查询数据有多少，超过某个数值不执行任务
            Integer batchAddressTask = redisClient.get("BATCH_ADDRESS_TASK");
            Integer thresholdValue = redisClient.get("THRESHOLD_VALUE");
            //log.info("udunConfig.getTimingTaskFlag():{},batchAddressTask:{}"+udunConfig.getTimingTaskFlag(),batchAddressTask);
            if (null == batchAddressTask) {
                batchAddressTask = addressPoolService.getBatchCount();
                redisClient.set("BATCH_ADDRESS_TASK", batchAddressTask, 1);
            }
            log.info("udunConfig.getTimingTaskFlag():" + maleChainConfig.getTimingTaskFlag());
            if (!maleChainConfig.getTimingTaskFlag().equals("do") || batchAddressTask > (thresholdValue == null ? 400000 : thresholdValue)) {
                log.info("未请求生成币种地址!!!");
                return;
            } else {
                log.info("请求生成币种地址!!!");
                int coinType = 60;
                Address address = maleChainService.createMaleChainAddress(coinType);
                if (null == address){ throw new BizException("请求生成币种地址为空!");}
                TAddressPool addressPool = new TAddressPool();
                addressPool.setAddress(address.getAddress());
                addressPoolService.addAddressTools(addressPool);
            }
        }catch (Exception e){
            log.info("获取公链地址出现异常:{}",e.toString(),e);
        }finally {
            log.info("获取公链地址finally");
        }
    }

    @Override
    public String getCronMapper() {
        String cronRedisKey = RedisConstant.APP_REDIS_PREFIX + "CRON_UDUN_ADDRESS";
        String redisValue = redisClient.get(cronRedisKey);
        if(null==redisValue){
            redisValue = cronMapper.getCreateAddressCron();
            redisClient.set(cronRedisKey,redisValue);
            return redisValue;
        }else{
            return redisValue;
        }
    }


}
