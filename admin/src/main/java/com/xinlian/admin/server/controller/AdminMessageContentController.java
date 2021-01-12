package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.AdminMessageContentService;
import com.xinlian.common.request.MessageContentReceiveReq;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息内容管理接口
 */
@Api(value = "消息内容管理接口")
@RestController
@RequestMapping(value = "/adminMessageContent")
public class AdminMessageContentController {
    @Autowired
    private AdminMessageContentService adminMessageContentService;

    @ApiOperation(value = "新增消息内容",httpMethod = "POST")
    @PostMapping("/createMessageContentReceive")
    public ResponseResult createMessageContentReceive(@RequestBody MessageContentReceiveReq req) {
        return adminMessageContentService.createMessageContentReceive(req);
    }
}
