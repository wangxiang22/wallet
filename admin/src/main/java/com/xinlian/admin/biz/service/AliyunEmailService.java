package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.redis.AdminLuaScriptRedisService;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.biz.dao.AliYunEmailConfigMapper;
import com.xinlian.biz.model.AliYunEmailConfigModel;
import com.xinlian.biz.model.AliyunEmailLogModel;
import com.xinlian.common.aliUtil.SendMailUtil;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AliyunEmailService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private AdminLuaScriptRedisService luaScriptRedisService;
    @Resource
    private AliYunEmailConfigMapper yunEmailConfigMapper;

    //获取某个邮箱默认发送次数
    private final String DEFAULT_EMAIL_INIT_NUM_KEY = RedisConstant.APP_REDIS_PREFIX + "DEFAULT_EMAIL_INIT_NUM";

    private final String DEFAULT_USE_SORT_KEY = RedisConstant.APP_REDIS_PREFIX + "DEFAULT_USE_SORT";

    private final String SEND_EMAIL_CONFIG_KEY = RedisConstant.APP_REDIS_PREFIX + "SEND_EMAIL_CONFIG";

    //TODO -分布式高并发下，会出现多发情况，需要考虑重入排队锁。这块就会慢，
    //TODO 出现把生产待发送邮件存放再消息队列中去，然后来消费，先进先消费
    //也可以使用redislock重入锁(60)后销毁
    public boolean sendEmailCode(String toEmailAddress,String emailCode,Integer useType) {
        //获取发送邮箱验证码信息，无从数据库获取
        AliYunEmailConfigModel configModel = redisClient.get(SEND_EMAIL_CONFIG_KEY);
        if (null == configModel) {
            log.info("缓存不存在，在DB中获取");
            configModel = yunEmailConfigMapper.nextUseSortEmail(null);
            redisClient.setDayResidueTimes(SEND_EMAIL_CONFIG_KEY,configModel);
            redisClient.setDayResidueTimes(DEFAULT_USE_SORT_KEY, configModel.getUseSort());
        }
        log.info("admin-发送邮件");
        long residueTimesSeconds = CommonUtil.getTheDayResidueSecond();
        Long residueSendNum = luaScriptRedisService.doIncr(DEFAULT_EMAIL_INIT_NUM_KEY,residueTimesSeconds);
        if(residueSendNum.intValue()>configModel.getSendInitNum().intValue()){
            configModel = yunEmailConfigMapper.nextUseSortEmail(configModel);
            luaScriptRedisService.deleteByLua(DEFAULT_EMAIL_INIT_NUM_KEY); //每个重新计数
            //下一个账号重新计数 --
            luaScriptRedisService.doIncr(DEFAULT_EMAIL_INIT_NUM_KEY,residueTimesSeconds);
            redisClient.setDayResidueTimes(SEND_EMAIL_CONFIG_KEY,configModel);
            redisClient.setDayResidueTimes(DEFAULT_USE_SORT_KEY, configModel.getUseSort());
        }
        boolean resultFlag = SendMailUtil.sampleLogin(configModel, emailCode, toEmailAddress, MailTemplateEnum.getEnumDesc(useType));
        log.error("运营后台发送邮件：resultFlag:{}--residueSendNum:{};configModel.getSendInitNum():{}", resultFlag, residueSendNum, configModel.getSendInitNum());
        threadSaveEmailLog(configModel.getEmailAddress(), emailCode, useType, toEmailAddress, resultFlag);
        //记录发送邮件日志
        return resultFlag;
    }

    private void threadSaveEmailLog(String fromEmailAddress, String emailCode, Integer useType, String toEmailAddress,boolean resultFlag) {
        log.info("记录日志:参数获取");
        AliyunEmailLogModel emailLogModel = new AliyunEmailLogModel();
        emailLogModel.setSenderEmailAddress(fromEmailAddress);
        emailLogModel.setAcceptorEmailAddress(toEmailAddress);
        emailLogModel.setEmailCode(emailCode);
        emailLogModel.setUseType(useType);
        emailLogModel.setUseTypeDesc(MailTemplateEnum.getEnumDesc(useType));
        emailLogModel.setEmailResult("result:"+resultFlag);
        log.info("记录日志:开始保存操作");
        yunEmailConfigMapper.threadSaveEmailLog(emailLogModel);
    }
}
