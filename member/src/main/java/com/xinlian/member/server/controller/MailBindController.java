package com.xinlian.member.server.controller;

import com.xinlian.common.request.BindMailReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.MailBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钱包用户绑定邮箱接口
 */
@Api(value = "钱包用户绑定邮箱接口")
@RestController
@RequestMapping("/{versionPath}/mailBind")
public class MailBindController {
    @Autowired
    private MailBindService mailBindService;

    @ApiOperation(value = "绑定邮箱",httpMethod = "POST")
    @PostMapping("/bindMail")
    @PassToken
    public ResponseResult bindMail(@RequestBody BindMailReq bindMailReq) {
        return mailBindService.bindMail(bindMailReq);
    }
}