package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.MailSendReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.AliyunEmailService;
import com.xinlian.member.server.controller.handler.RegisterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Song
 * @date 2020-08-12 09:56
 * @description 发送邮箱控制器 - html5
 */
@Api(value = "大航海注册相关")
@Slf4j
@RestController
@RequestMapping("/seaPatrol")
public class Html5OldSendEmailRegisterController {

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


    @ApiOperation(value = "发送邮箱验证码", httpMethod = "POST")
    @RequestMapping(value = "/mail/send", method = RequestMethod.POST)
    @PassToken
    public ResponseResult sendRegisterMail(@RequestBody Map<String,String> paramMap) {
        boolean lockFlag = true;
        MailSendReq req = registerHandler.decodeDataToObject(paramMap,MailSendReq.class);
        Lock lock = redisLockRegistry.obtain(req.getEmail() + req.getNodeId());
        if (null==req.getEmail() || !req.getEmail()
                .matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
            throw new BizException("邮箱格式有误!");
        }
        try {
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
}
