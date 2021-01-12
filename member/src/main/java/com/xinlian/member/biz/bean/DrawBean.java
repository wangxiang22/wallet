//package com.xinlian.member.biz.bean;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import com.xinlian.biz.model.LotteryDraw;
//import com.xinlian.member.biz.redis.RedisClient;
//import com.xinlian.member.biz.service.LotteryDrawService;
//import lombok.Data;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.List;
//
//import static com.xinlian.member.biz.redis.RedisConstant.DrawPrize;
//
///**
// * 抽奖信息初始化
// */
//
//@Component
//@Data
//@Slf4j
//public class DrawBean {
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private LotteryDrawService lotteryDrawService;
//
//    @Bean
//    public void initRedisDrawInfo() {
//        List<LotteryDraw> draws = redisClient.hashValues(DrawPrize);
//        if (draws == null||draws.isEmpty()) {
//            List<LotteryDraw> list = lotteryDrawService.selectList(new EntityWrapper<LotteryDraw>().orderBy("id", true));
////        redisClient.set(DrawPrize, JSON.toJSONString(list));
//            ImmutableMap<String, LotteryDraw> stringLotteryDrawImmutableMap = Maps.uniqueIndex(list, LotteryDraw::getCode);
//            redisClient.hashAll(DrawPrize, stringLotteryDrawImmutableMap);
//        }
//        log.info("奖项信息初始化成功");
//    }
//}
