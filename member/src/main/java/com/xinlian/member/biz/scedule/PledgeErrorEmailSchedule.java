//package com.xinlian.member.biz.scedule;
//
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.xinlian.biz.dao.TPledgeMiningLogMapper;
//import com.xinlian.biz.model.TPledgeMiningLog;
//import com.xinlian.common.enums.MailTemplateEnum;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.scedule.AbstractSchedule;
//import com.xinlian.member.biz.service.AliyunEmailService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.integration.redis.util.RedisLockRegistry;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.locks.Lock;
//
//@Component
//@Slf4j
//public class PledgeErrorEmailSchedule extends AbstractSchedule {
//    @Autowired
//    private AliyunEmailService aliyunEmailService;
//    @Autowired
//    private TPledgeMiningLogMapper pledgeMiningLogMapper;
//    @Autowired
//    private RedisLockRegistry redisLockRegistry;
//
//    /**
//     * 获取cron表达式
//     * @return 每天10点执行一次
//     */
//    @Override
//    protected String getCron() {
//        return "0 0 10 * * ?";
//    }
//
//    @Override
//    public void doSchedule() {
//        Lock lock = redisLockRegistry.obtain("PLEDGE_ERROR_MAIL");
//        boolean redisLockFlag = true;
//        try {
//            if(!lock.tryLock()){
//                log.debug(Thread.currentThread().getName()+" : 质押推送失败提醒邮件发送失败，获取分布式锁失败!");
//                redisLockFlag = false;
//            }
//            //请求算力地球接口推送状态 - 1：推送成功，2：推送失败，3：待再次推送（此数值手动修改）
//            List<TPledgeMiningLog> statusErrorList = pledgeMiningLogMapper.selectList(new EntityWrapper<TPledgeMiningLog>().eq("status", 2));
//            if (null != statusErrorList && statusErrorList.size() > 0) {
//                String toEmailAddress = "liuting@merkletrees.cn";
//                boolean sendFlag = aliyunEmailService.sendEmailCode(toEmailAddress,"", MailTemplateEnum.PLEDGE_ERROR_SCHEDULE.getCode());
//                if(!sendFlag){
//                    throw new BizException("发送邮件失败,请稍后重试!");
//                }
//            }
//        } catch (BizException e) {
//            log.error("质押推送失败提醒邮件发送结果异常");
//        } finally {
//            if(redisLockFlag) {
//                lock.unlock();
//                log.debug(Thread.currentThread().getName()+" : 质押推送失败提醒邮件发送失败，释放分布式锁success");
//            }
//            log.debug(Thread.currentThread().getName()+" : 质押推送失败提醒邮件发送失败，释放分布式锁失败!");
//        }
//    }
//}
