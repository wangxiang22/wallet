package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.CheckSmsMethodEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.NullParam;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.UserMessageRes;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.TUserMessage;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TUserMessageImpl implements TUserMessage {
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private IRegisterLoginService registerLoginService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ChuangLanSmsService chuangLanSmsService;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;

    //发送查询用户信息的验证码
    @Override
    public ResponseResult sendQuerySms(RegisterReq req){
        req.setType(12);
        ResponseResult result  = new ResponseResult();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        result.setResult(new NullParam());
        //获取随机码
        String code = EncryptionUtil.getRandomCode(6);
        //国内 短信
        String phone = req.getCountryCode()==86 ? req.getPhone() : req.getCountryCode()+req.getPhone();
        if(!canSend(phone)){
           result.setMsg("当日查询次数已经超过上限，请24小时后再次查询。");
           return result;
        }
        String redisKey = pushCodeToRedis(RedisKeys.createSmsPhoneKey(req.getType(), phone), code);
        boolean flag = false;
        if(req.getCountryCode() == 86){
            flag = chuangLanSmsService.sendRegisterCodeCh(phone, code,null,redisKey);
        }else {
            //国际 短信
            flag = chuangLanSmsService.sendRegisterCodeInte(phone, code);
        }
        if(flag){
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        }else {
            result.setMsg("验证码发送失败，稍后重试");
            result.setCode(GlobalConstant.ResponseCode.FAIL);
        }
        return result;
    }

    //设置过期时间
    private boolean canSend(String phone){
        String key = RedisKeys.phoneKey(phone);
        if(redisClient.setNx(key, 0)){
            redisClient.expire(key, 24*60*60);
        }
        //限制次数，一天只能发送5次
        long count = redisClient.increment(key, 1);
        if(count > 5){
            return false;
        }
        return true;
    }

//验证码5分钟过期
    private String pushCodeToRedis(String phone, String code){
        String phoneKey = SmsUtil.createPhoneKey(phone);
        redisClient.set(phoneKey, code);
        redisClient.expire(phoneKey, 5 * 60);
        return phoneKey;
    }


    //查询用户信息，根据手机号，验证验证码
    @Override
    public ResponseResult<List<UserMessageRes>> queryUserName(RegisterReq registerReq) {
        ResponseResult <List<UserMessageRes>> result = new ResponseResult();
        List<UserMessageRes> userMessageRes = null;
        registerReq.setType(12);
        String phone = SmsUtil.getCountryCodeAndPhone(registerReq.getPhone(),registerReq.getCountryCode());
        //验证手机号码是否验证错误次数过多
        checkSmsRuleHandler.doCheckSmsRuleHandler(phone, CheckSmsMethodEnum.SMS_QUERY_ACCOUNT_NUMBER.getMethodCode());
        boolean b = registerLoginService.checkCode(registerReq.getType(), phone, registerReq.getCode());
        if (b){
            checkSmsRuleHandler.doDeleteSmsRuleHandler(phone,CheckSmsMethodEnum.SMS_QUERY_ACCOUNT_NUMBER.getMethodCode());
            userMessageRes = userInfoMapper.queryUserName(registerReq.getPhone());
            result.setMsg("请求成功");
            result.setCode(GlobalConstant.ResponseCode.SUCCESS);
            result.setResult(userMessageRes);
            return result;
        }
        result.setMsg("验证码已经过期，请稍后重试");
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        return result;
    }
}
