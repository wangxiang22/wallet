package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.AdminMiningApplyService;
import com.xinlian.biz.model.TMiningApply;
import com.xinlian.common.request.FindAllUserReq;
import com.xinlian.common.request.MiningConfigReq;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminMiningApply")
@Api("挖矿后台")
public class AdminMiningApplyController {
    @Autowired
    private AdminMiningApplyService adminMiningApplyService;

    @ApiOperation("设置激活时间，下次激活时间，激活名额")
    @PostMapping("setConfig")
    public ResponseResult setConfig(@RequestBody MiningConfigReq miningConfigReq){
        adminMiningApplyService.setConfig(miningConfigReq);
        return ResponseResult.ok();
    }

    @ApiOperation("审核用户")
    @PostMapping("passUser")
    public ResponseResult passUser(@RequestBody TMiningApply tMiningApply){
        adminMiningApplyService.passUser(tMiningApply);
        return ResponseResult.ok();
    }

    @ApiOperation("查询所有审核用户")
    @PostMapping("findAllUser")
    public ResponseResult findAllUser(@RequestBody FindAllUserReq findAllUserReq){
        return adminMiningApplyService.findAllUser(findAllUserReq);
    }
}
