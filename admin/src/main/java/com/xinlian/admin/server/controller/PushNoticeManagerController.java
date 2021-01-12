package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.PushNoticeManagerService;
import com.xinlian.biz.model.TPushNotice;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推送通知管理接口
 */
@Api(value = "推送通知管理接口")
@RestController
@RequestMapping(value = "/pushNoticeManager")
public class PushNoticeManagerController {
    @Autowired
    private PushNoticeManagerService pushNoticeManagerService;

    @ApiOperation(value = "新增定时推送",httpMethod = "POST")
    @PostMapping("/createPushNotice")
    public ResponseResult createPushNotice(@RequestBody TPushNotice tPushNotice) {
        return pushNoticeManagerService.createPushNotice(tPushNotice);
    }

    @ApiOperation(value = "查询全部推送通知信息（分页）",httpMethod = "POST")
    @PostMapping("/findPushNoticeListPage")
    public PageResult<List<TPushNotice>> findPushNoticeListPage(@RequestBody PageReq pageReq) {
        return pushNoticeManagerService.findPushNoticeListPage(pageReq);
    }

    @ApiOperation(value = "提前下线推送通知",httpMethod = "POST")
    @PostMapping("/deletePushNotice")
    public ResponseResult deletePushNotice(@ApiParam(name = "id",value = "推送通知的主键id",required = true)
                                           @RequestParam Long id) {
        return pushNoticeManagerService.deletePushNotice(id);
    }
}
