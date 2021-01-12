package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.dao.AliYunEmailConfigMapper;
import com.xinlian.biz.model.AliYunEmailConfigModel;
import com.xinlian.biz.model.AliyunEmailLogModel;
import com.xinlian.common.aliUtil.SendMailUtil;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.AliyunEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * com.xinlian.member.biz.service.impl
 *
 * @author by Song
 * @date 2020/2/9 20:15
 */
@Service
@Slf4j
public class AliyunEmailServiceImpl implements AliyunEmailService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Resource
    private AliYunEmailConfigMapper yunEmailConfigMapper;
    @Resource
    private AdminOptionsMapper adminOptionsMapper;

    //获取某个邮箱默认发送次数
    private final String DEFAULT_EMAIL_INIT_NUM_KEY = RedisConstant.APP_REDIS_PREFIX + "DEFAULT_EMAIL_INIT_NUM";

    private final String DEFAULT_USE_SORT_KEY = RedisConstant.APP_REDIS_PREFIX + "DEFAULT_USE_SORT";

    private final String SEND_EMAIL_CONFIG_KEY = RedisConstant.APP_REDIS_PREFIX + "SEND_EMAIL_CONFIG";

    //TODO -分布式高并发下，会出现多发情况，需要考虑重入排队锁。这块就会慢，
    //TODO 出现把生产待发送邮件存放再消息队列中去，然后来消费，先进先消费
    //也可以使用redislock重入锁(60)后销毁
    @Override
    public boolean sendEmailCode(String toEmailAddress,String emailCode,Integer useType) {
        //获取发送邮箱验证码信息，无从数据库获取
        AliYunEmailConfigModel configModel = redisClient.get(SEND_EMAIL_CONFIG_KEY);
        if (null == configModel) {
            configModel = yunEmailConfigMapper.nextUseSortEmail(null);
            redisClient.setDayResidueTimes(SEND_EMAIL_CONFIG_KEY,configModel);
            redisClient.setDayResidueTimes(DEFAULT_USE_SORT_KEY, configModel.getUseSort());
        }
        long residueTimesSeconds = CommonUtil.getTheDayResidueSecond();
        //发送量减1 - 直到0(或者增加到发送量) ，换账号信息，循环上一步的两个key - value 赋值
        Long residueSendNum = luaScriptRedisService.doIncr(DEFAULT_EMAIL_INIT_NUM_KEY,residueTimesSeconds);
        if(residueSendNum.intValue()>configModel.getSendInitNum().intValue()){
            configModel = yunEmailConfigMapper.nextUseSortEmail(configModel);
            if(null==configModel){
                throw new BizException("发送邮件短信验证码异常，请尝试绑定手机号码，发送短信验证码！");
            }
            luaScriptRedisService.deleteByLua(DEFAULT_EMAIL_INIT_NUM_KEY); //每个重新计数
            //下一个账号重新计数 --
            luaScriptRedisService.doIncr(DEFAULT_EMAIL_INIT_NUM_KEY,residueTimesSeconds);
            redisClient.setDayResidueTimes(SEND_EMAIL_CONFIG_KEY,configModel);
            redisClient.setDayResidueTimes(DEFAULT_USE_SORT_KEY, configModel.getUseSort());
        }
        boolean resultFlag = SendMailUtil.sample(configModel, emailCode, toEmailAddress,MailTemplateEnum.getEnumDesc(useType));
        log.error("发送邮件：resultFlag:{}--residueSendNum:{};configModel.getSendInitNum():{}", resultFlag, residueSendNum, configModel.getSendInitNum());
        threadSaveEmailLog(configModel.getEmailAddress(), emailCode, useType, toEmailAddress, resultFlag);
        //记录发送邮件日志
        return resultFlag;
    }

    private void threadSaveEmailLog(String fromEmailAddress, String emailCode, Integer useType, String toEmailAddress,boolean resultFlag) {
        AliyunEmailLogModel emailLogModel = new AliyunEmailLogModel();
        emailLogModel.setSenderEmailAddress(fromEmailAddress);
        emailLogModel.setAcceptorEmailAddress(toEmailAddress);
        emailLogModel.setEmailCode(emailCode);
        emailLogModel.setUseType(useType);
        emailLogModel.setUseTypeDesc(MailTemplateEnum.getEnumDesc(useType));
        emailLogModel.setEmailResult("result:"+resultFlag);
        yunEmailConfigMapper.threadSaveEmailLog(emailLogModel);
    }


    @Override
    public void checkOftenFlag(String emailAddress, Integer useType) {
        String inTheTimeKey = RedisConstant.APP_REDIS_PREFIX+"EMAIL_IN_THE_TIME";
        String sendNumKey = RedisConstant.APP_REDIS_PREFIX+"EMAIL_SEND_NUM";
        Integer sendNum = getAdminOptionsValue(sendNumKey);
        Integer inTheTimeValue = getAdminOptionsValue(inTheTimeKey);
        if(null!=sendNum && null!=inTheTimeValue){
            String incrKey = RedisConstant.APP_REDIS_PREFIX + emailAddress + "_" + useType;
            Long incrNum = luaScriptRedisService.doIncr(incrKey);
            if(incrNum.intValue()>sendNum.intValue()){
                throw new BizException("发送太频繁清稍候重试");
            }else if(incrNum.intValue()==1){
                redisClient.set(incrKey,1,Long.parseLong(inTheTimeValue+""));
            }
        }
    }

    public Integer getAdminOptionsValue(String optionsName){
        String getValue = redisClient.get(optionsName);
        if(null==getValue){
            String optionsValue = adminOptionsMapper.getAdminOptionValueByKey(optionsName);
            if(null == optionsValue){return null;}
            redisClient.set(optionsName,optionsValue);
            return new Integer(optionsValue);
        }
        return new Integer(getValue);
    }
}
