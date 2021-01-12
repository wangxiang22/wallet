package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.model.Address;
import com.xinlian.biz.model.TrcUsdtAddressPool;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TrcUsdtAddressPoolService;
import com.xinlian.member.biz.service.TrcUsdtService;
import com.xinlian.member.biz.trcusdt.TrcUsdtConfig;
import com.xinlian.member.biz.udun.CompleteScheduleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@Slf4j
public class TrcUsdtAddressHandler extends CompleteScheduleConfig {

    @Autowired
    private TrcUsdtService trcUsdtService;
    @Autowired
    private TrcUsdtAddressPoolService trcUsdtAddressPoolService;
    @Autowired
    private TrcUsdtConfig trcUsdtConfig;
    @Autowired
    private RedisClient redisClient;


    //3.添加定时任务
    //或直接指定时间间隔，例如：5秒
    @Override
    public void doSubClassTask() {
        try {
            if (!trcUsdtConfig.getTimingTaskFlag().equals("do")) {
                log.info("未请求TRC_USDT_生成币种地址!!!");
                return;
            } else {
                log.info("请求TRC_USDT_生成币种地址!!!");
                Address address = trcUsdtService.createTrcUsdtAddress("TRC20_USDT");
                if (null == address){ throw new BizException("请求生成币种地址为空!");}
                TrcUsdtAddressPool addressPool = new TrcUsdtAddressPool();
                addressPool.setAddressBase58(address.getAddress());
                trcUsdtAddressPoolService.addTrcUsdtAddressTools(addressPool);
            }
        }catch (BizException e){
            log.info("获取TRC_USDT_公链地址出现业务异常:{}",e.getMsg(),e);
        }catch (Exception e){
            log.info("获取TRC_USDT_公链地址出现系统异常:{}",e.toString(),e);
        }finally {
            log.info("获取TRC_USDT_公链地址finally");
        }
    }

    @Override
    public String getCronMapper() {
        String cronRedisKey = RedisConstant.APP_REDIS_PREFIX + "CRON_TRC_USDT_ADDRESS";
        String redisValue = redisClient.get(cronRedisKey);
        if(null==redisValue){
            redisValue = cronMapper.getTrcUsdtAddressCron();
            redisClient.set(cronRedisKey,redisValue);
            return redisValue;
        }else{
            return redisValue;
        }
    }


}
