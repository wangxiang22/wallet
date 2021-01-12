package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.enums.CheckSmsMethodEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.annotate.EncryptionAnnotation;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.TUserMessage;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/{versionPath}/userMessage")
@Api("根据用户手机号查用户信息")
public class TUserMessageController {

    @Autowired
    private TUserMessage userMessage;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;

    @ApiOperation("根据用户的手机号查对应账户信息")
    @PassToken
    @PostMapping("queryUserMessage")
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    @EncryptionAnnotation
    public ResponseResult queryUserMessage(@RequestBody Map<String,String> paramMap) {
        RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"),RegisterReq.class);
        return userMessage.queryUserName(registerReq);
    }


    @ApiOperation("查询工具-发送验证码")
    @PassToken
    @PostMapping("sendQuerySms")
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    @EncryptionAnnotation
    public ResponseResult sendQuerySms(@RequestBody Map<String,String> paramMap) {
        RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"),RegisterReq.class);
        checkSmsRuleHandler.doCheckSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(registerReq.getPhone(),registerReq.getCountryCode()), CheckSmsMethodEnum.SMS_QUERY_ACCOUNT_NUMBER.getMethodCode());
        return userMessage.sendQuerySms(registerReq);
    }

}

