package com.xinlian.admin.server.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.service.BindExchangeInfoService;
import com.xinlian.common.request.QueryBindInfoReq;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("绑定交易所记录")
@RequestMapping("bindExchangeInfo")
@RestController
public class BindExchangeInfoController {
    @Autowired
    BindExchangeInfoService bindExchangeInfoService;
    @ApiOperation("查询绑定详情")
    @PostMapping("queryBindInfo")
    public ResponseResult queryBindInfo(QueryBindInfoReq queryBindInfo) {
        return bindExchangeInfoService.queryBindInfo(queryBindInfo);
    }
}
