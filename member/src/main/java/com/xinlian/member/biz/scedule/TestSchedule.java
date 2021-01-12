//package com.xinlian.member.biz.scedule;
//
//import com.google.common.collect.Maps;
//import com.xinlian.biz.dao.LotteryDrawPrizerMapper;
//import com.xinlian.biz.dao.TUserInfoMapper;
//import com.xinlian.biz.model.LotteryDrawPrizer;
//import com.xinlian.common.dto.UserInfoDto;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.scedule.AbstractSchedule;
//import com.xinlian.member.biz.redis.RedisClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import java.util.*;
//
//import static com.xinlian.member.biz.redis.RedisConstant.DRAWER;
//import static com.xinlian.member.biz.redis.RedisConstant.USER_ACTIVE;
//
///**
// * @author: cms
// * @description:
// * @create: 2020/01/28
// * @des: 测试
// **/
//@Component
//@Slf4j
//public class TestSchedule extends AbstractSchedule {
//    @Autowired
//    private TUserInfoMapper tUserInfoMapper;
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private LotteryDrawPrizerMapper lotteryDrawPrizerMapper;
//
//    /**
//     * 获取cron表达式
//     *
//     * @return
//     */
//    @Override
//    protected String getCron() {
//        return "0/5 * * * * ?";
//    }
//
//    @Autowired
//    private TransactionTemplate transactionTemplate;
//
//    /**
//     * 具体执行的定时任务
//     */
//    long i = 1;
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void doSchedule() {
//
//
//
//    }
//
//}
//
//
