package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.cachekey.SmsTypeEnum;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.MailSendReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import com.xinlian.member.biz.jwt.annotate.EncryptionAnnotation;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.AliyunEmailService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.RegisterHandler;
import com.xinlian.member.server.vo.request.register.FinishBindMobileSendSmsRequest;
import com.xinlian.member.server.vo.request.register.RegisterFinishBindMobileRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Song
 * @date 2020-08-12 09:56
 * @description 发送邮箱控制器
 */
@Api(value = "大航海注册相关")
@Slf4j
@RestController
@RequestMapping("/{versionPath}/seaPatrol")
public class SendEmailRegisterController {

    @Autowired
    private RegisterHandler registerHandler;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private AliyunEmailService aliyunEmailService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private ChuangLanSmsService chuangLanSmsService;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;


    @ApiOperation(value = "发送邮箱验证码", httpMethod = "POST")
    @RequestMapping(value = "/mail/send", method = RequestMethod.POST)
    @PassToken
    @EncryptionAnnotation
    public ResponseResult sendRegisterMail(@RequestBody Map<String,String> paramMap) {
        boolean lockFlag = true;
        MailSendReq req = JSONObject.parseObject(paramMap.get("data"),MailSendReq.class);
        Lock lock = redisLockRegistry.obtain(req.getEmail() + req.getNodeId());
        if (!req.getEmail()
                .matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
            throw new BizException("邮箱格式有误!");
        }
        try {
            registerHandler.checkAdPercent(req.getAsSubPre());
            if(StringUtils.isEmpty(req.getUseType())
                    || !nodeVoyageUtil.belongVoyageNode(req.getNodeId())
                    || MailTemplateEnum.REGISTER_TYPE.getCode().intValue() != req.getUseType()){
                throw new BizException("校验参数错误!");
            }
            if (!lock.tryLock()) {
                lockFlag = false;
                throw new BizException("邮箱验证码发送中,请耐心等待!");
            }
            //check email register number
            registerHandler.judgeAbroadNodeIsEmailRegister(req.getNodeId(),req.getEmail());
            if (null != req.getUseType() && nodeVoyageUtil.belongVoyageNode(req.getNodeId())) {
                aliyunEmailService.checkOftenFlag(req.getEmail(), req.getUseType());
                String emailCode = EncryptionUtil.getRandomCode(6);
                boolean flag = aliyunEmailService.sendEmailCode(req.getEmail(), emailCode, req.getUseType());
                // 邮箱验证码-KEY
                String emailKey = RedisKeys.getSendEmailKey(RedisConstant.EMAIL_CODE_KEY_PREFIX , req.getUseType(),req.getEmail());
                redisClient.set(emailKey, emailCode, 8 * 60);
                if (!flag) {
                    throw new BizException("发送邮箱验证码出现异常，请稍候重试!");
                }
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e) {
            log.error(DateFormatUtil.getByNowTime(7)+"发送邮箱验证码出现业务异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.getMsg())));
            return new ResponseResult(ErrorInfoEnum.FAILED.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.getByNowTime(7)+"发送邮箱验证码出现系统异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.toString())));
            return new ResponseResult(new BizException("发送邮箱验证码出现异常，请稍后重试!"));
        }finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
    }

    @ApiOperation(value = "大航海下注册完成绑定手机号码", httpMethod = "POST")
    @PostMapping(value = "/bindPhone")
    @PassToken
    @EncryptionAnnotation
    public ResponseResult registerFinishBindPhone(@RequestBody Map<String,String> paramMap) {
        RegisterFinishBindMobileRequest bindMobileRequest = JSONObject.parseObject(paramMap.get("data"),RegisterFinishBindMobileRequest.class);
        boolean lockFlag = true;
        Lock lock = redisLockRegistry.obtain("/register/bindPhone".concat(bindMobileRequest.getMobile()));
        try {
            if(StringUtils.isEmpty(bindMobileRequest.getMobile())
                    || StringUtils.isEmpty(bindMobileRequest.getMobileSmsCode())){
                throw new BizException("校验参数错误!");
            }
            if (!lock.tryLock()) {
                lockFlag = false;
                throw new BizException("正在绑定手机号,请耐心等待!");
            }
            //获取userInfo
            TUserInfo getUserInfo = this.getUserInfo(bindMobileRequest.getUserName(),bindMobileRequest.getNodeId());
            if(!StringUtils.isEmpty(getUserInfo.getMobile())){
                throw new BizException("该账户已经绑定过手机号码!");
            }
            //check email register number
            if (nodeVoyageUtil.belongVoyageNode(getUserInfo.getServerNodeId())) {
                registerHandler.judgeAbroadNodeIsRegister(getUserInfo.getServerNodeId(),bindMobileRequest.getMobile());
                //检验smsCode --
                String redisKey = RedisKeys.getSendSmsKey(SmsTypeEnum.SEA_PATROL_REGISTER_BIND_MOBILE);
                if(!bindMobileRequest.getMobileSmsCode().equals(redisClient.get(redisKey))){
                    checkSmsRuleHandler.doSaveSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(bindMobileRequest.getMobile(),getUserInfo.getCountryCode()),SmsTypeEnum.SEA_PATROL_REGISTER_BIND_MOBILE.getCacheKey());
                    throw new BizException("短信验证码错误或过期!");
                }
                checkSmsRuleHandler.doDeleteSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(bindMobileRequest.getMobile(),getUserInfo.getCountryCode()),SmsTypeEnum.SEA_PATROL_REGISTER_BIND_MOBILE.getCacheKey());
                //修改
                TUserInfo updateUserInfo = new TUserInfo();
                updateUserInfo.setUid(getUserInfo.getUid());
                updateUserInfo.setMobile(bindMobileRequest.getMobile());
                userInfoMapper.updateById(updateUserInfo);
                //delete sms key
                redisClient.deleteByKey(redisKey);
            }else{
                throw new BizException("节点信息有误，请核实!");
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e) {
            log.error(DateFormatUtil.getByNowTime(7)+"大航海下注册完成绑定手机号码出现业务异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.getMsg())));
            return new ResponseResult(ErrorInfoEnum.FAILED.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.getByNowTime(7)+"大航海下注册完成绑定手机号码出现系统异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.toString())));
            return new ResponseResult(new BizException("绑定手机号码出现异常，请稍后重试!"));
        }finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
    }

    @ApiOperation(value = "大航海下注册完成绑定手机号码 - 发送短信验证码", httpMethod = "POST")
    @PostMapping(value = "/bindPhone/sendSms")
    @PassToken
    @EncryptionAnnotation
    public ResponseResult bindPhoneSendSms(@RequestBody Map<String,String> paramMap) {
        FinishBindMobileSendSmsRequest sendSmsRequest = JSONObject.parseObject(paramMap.get("data"),FinishBindMobileSendSmsRequest.class);
        boolean lockFlag = true;
        Lock lock = redisLockRegistry.obtain("/bindPhone/sendSms".concat(sendSmsRequest.getMobile()));
        try {
            if(StringUtils.isEmpty(sendSmsRequest.getMobile())
                ||StringUtils.isEmpty(sendSmsRequest.getNodeId())
                ||StringUtils.isEmpty(sendSmsRequest.getUserName())){
                throw new BizException("校验参数错误!");
            }
            if (!lock.tryLock()) {
                lockFlag = false;
                throw new BizException("正在发送手机验证码,请耐心等待!");
            }
            //check email register number
            if (nodeVoyageUtil.belongVoyageNode(sendSmsRequest.getNodeId())) {
                //检测手机号码节点下绑定了多少个
                registerHandler.judgeAbroadNodeIsRegister(sendSmsRequest.getNodeId(),sendSmsRequest.getMobile());
                //获取userInfo
                TUserInfo getUserInfo = this.getUserInfo(sendSmsRequest.getUserName(),sendSmsRequest.getNodeId());
                if(!StringUtils.isEmpty(getUserInfo.getMobile())){
                    throw new BizException("该账户已经绑定过手机号码!");
                }
                //检验这个手机号码是否具有资格访问这个接口
                checkSmsRuleHandler.doCheckSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(sendSmsRequest.getMobile(),getUserInfo.getCountryCode()),SmsTypeEnum.SEA_PATROL_REGISTER_BIND_MOBILE.getCacheKey());
                this.sendBindPhoneSms(getUserInfo.getCountryCode(),sendSmsRequest.getMobile());
            }else{
                throw new BizException("节点信息有误，请核实!");
            }
            return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
        } catch (BizException e) {
            log.error(DateFormatUtil.getByNowTime(7)+"大航海下注册发送验证码出现业务异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.getMsg())));
            return new ResponseResult(ErrorInfoEnum.FAILED.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.getByNowTime(7)+"大航海下注册发送验证码出现系统异常：{}",
                    JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.toString())));
            return new ResponseResult(new BizException("绑定手机号码出现异常，请稍后重试!"));
        }finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
    }

    @Async
    public void sendBindPhoneSms(Integer countryCode, String mobile) {
        //发送smsCode -- cacheKey
        String redisKey = RedisKeys.getSendSmsKey(SmsTypeEnum.SEA_PATROL_REGISTER_BIND_MOBILE);
        String code = EncryptionUtil.getRandomCode(6);
        if(86==countryCode){
            throw new BizException("请确认信息!");
        }else{
            String phone = countryCode + mobile;
            chuangLanSmsService.sendRegisterCodeInte(phone,code,null,redisKey);
        }
        redisClient.set(redisKey,code);
    }

    private TUserInfo getUserInfo(String userName,Long nodeId){
        TUserInfo whereUserInfo = new TUserInfo();
        whereUserInfo.setUserName(userName);
        whereUserInfo.setServerNodeId(nodeId);
        TUserInfo getUserInfo = userInfoMapper.getOneModel(whereUserInfo);
        if(null==getUserInfo){throw new BizException("请确认信息!");}
        return getUserInfo;
    }
}
