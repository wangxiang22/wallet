package com.xinlian.member.server.controller;

import com.xinlian.common.request.PushNoticeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.PushNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推送通知接口
 */
@Api(value = "推送通知接口")
@RestController
@RequestMapping("/{versionPath}/pushNotice")
public class PushNoticeController {
    @Autowired
    private PushNoticeService pushNoticeService;

    @ApiOperation(value = "查询推送的结束时间、有值的uid列表或者有值的节点id列表。自定义消息中字段含义：title - 推送标题，closeStatus - 是否可以关闭（0：不可关闭，1：可以关闭），" +
            "type - 推送类型（1：图片推送，2：文字推送，3：全屏推送），uuid - 唯一标识码，image - 推送图片，url - 图片点击后跳转链接，text - 推送文字，fullScreen - 全屏推送链接",
            httpMethod = "POST")
    @PostMapping("/findNoticeEndTime")
    public ResponseResult findNoticeEndTime(@ApiParam(name = "uniqueCode",value = "唯一标识码",required = true)
                                            @RequestBody PushNoticeReq pushNoticeReq) {
        return pushNoticeService.findNoticeEndTime(pushNoticeReq);
    }
}
