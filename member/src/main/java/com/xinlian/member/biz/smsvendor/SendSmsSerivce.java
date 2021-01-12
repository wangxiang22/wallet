package com.xinlian.member.biz.smsvendor;

import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.SmsSenderChoiceEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.smsvendor.base.SendSmsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Song
 * @date 2020-07-08 16:21
 * @description 发送短信服务
 */
@Slf4j
@Component
public class SendSmsSerivce {

//    private SmsVendorService SmsVendorService;
//    //构造函数，要你使用哪个妙计
//    public SendSmsSerivce(SmsVendorService smsVendorService){
//        this.SmsVendorService = smsVendorService;
//    }
//
//    private void doSendSmsOther(SendSmsModel sendSmsModel){
//        this.SmsVendorService.doSendSmsOther(sendSmsModel);
//    }
//
//    private void doSendSmsChina(SendSmsModel sendSmsModel){
//        this.SmsVendorService.doSendSmsChina(sendSmsModel);
//    }

    public void sendSms(SendSmsModel sendSmsModel){
        //1.获取配置信息-拿到排序第一的对应参数

        //if()
    }
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    /**
     * 保存发发送记录数
     * @param smsSenderChoiceEnum 所处厂商
     * @param phone 手机号
     */
    public void saveSendSmsNumberOfTimes(SmsSenderChoiceEnum smsSenderChoiceEnum, String phone){
        //check system phone White List
        if(checkPhoneWhileList(phone)){
            log.info(DateFormatUtil.get(7,new Date()) + "短信厂商白名单:" + phone);
            return;
        }
        String incrSendSuccessNumKey = smsSenderChoiceEnum.getCode() + phone;
        String getHourKey = this.getCheckHourRuleKey(incrSendSuccessNumKey);
        String getHalfDayKey = this.getCheckHalfDayRuleKey(incrSendSuccessNumKey);
        luaScriptRedisService.doIncr(getHourKey,1*60*60L);
        luaScriptRedisService.doIncr(getHalfDayKey,12*60*60L);
    }

    /**
     * check system phone White List
     * @param phone
     * @return
     */
    private boolean checkPhoneWhileList(String phone) {
        String whiteListPhones = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.PHONE_WHITE_LIST.getBelongsSystemCode());
        return whiteListPhones.contains(phone);
    }


    /**
     * check 短信次数
     * @param smsSenderChoiceEnum
     * @param phone
     */
    public void checkSendSmsNumberOfTimes(SmsSenderChoiceEnum smsSenderChoiceEnum,String phone){
        checkSendSmsNumberOfTimesByStr(smsSenderChoiceEnum.getCode(),phone);
    }

    public void checkSendSmsNumberOfTimesByStr(String enumCode,String phone){
        //check system phone White List
        if(checkPhoneWhileList(phone)){
            log.info("check sms white list " + DateFormatUtil.get(7,new Date()) + "短信厂商白名单:" + phone);
            return;
        }
        String incrSendSuccessNumKey = enumCode + phone;
        String getHourKey = this.getCheckHourRuleKey(incrSendSuccessNumKey);
        String getHalfDayKey = this.getCheckHalfDayRuleKey(incrSendSuccessNumKey);
        Integer hourSendNumber = redisClient.get(getHourKey);
        Integer halfDaySendNumber = redisClient.get(getHalfDayKey);
        String smsHourLimitNumStr = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SMS_HOUR_LIMIT_SEND_NUM.getBelongsSystemCode());//7
        String smsHalfDayLimitNumStr = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SMS_HALF_DAY_LIMIT_SEND_NUM.getBelongsSystemCode());//14
        int checkHourLimitSendNum = 10;
        int checkHalfDayLimitSendNum = 30;

        if(null!=smsHourLimitNumStr){
            checkHourLimitSendNum = Integer.valueOf(smsHourLimitNumStr);//Long.parseLong(smsHourErrorNumStr);
        }
        if(null!=smsHalfDayLimitNumStr){
            checkHalfDayLimitSendNum = Integer.valueOf(smsHalfDayLimitNumStr);
        }
        if(null!=hourSendNumber && hourSendNumber.intValue() >= checkHourLimitSendNum && hourSendNumber.intValue()< checkHalfDayLimitSendNum){
            log.error(DateFormatUtil.get(7,new Date())+"check sms send rule key:" + getHourKey);
            throw new BizException("短信发送超过"+hourSendNumber+"次,请一小时后尝试!");
        }
        if(null!=halfDaySendNumber && halfDaySendNumber.intValue() >= checkHalfDayLimitSendNum){
            log.error(DateFormatUtil.get(7,new Date())+"check sms send rule key:" + getHalfDayKey);
            throw new BizException("短信发送超过"+halfDaySendNumber+"次,请十二小时后尝试!");
        }
    }

    private String getCheckHourRuleKey(String redisKeySuffix){
        return RedisConstant.APP_REDIS_PREFIX + "1_" + redisKeySuffix;
    }

    private String getCheckHalfDayRuleKey(String redisKeySuffix){
        return RedisConstant.APP_REDIS_PREFIX + "12_" + redisKeySuffix;
    }

}
