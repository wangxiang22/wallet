package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.ExchangeWalletService;
import com.xinlian.common.request.FindExchangeBindStateReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.HttpClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/exchangeWallet")
@Api
public class ExchangeBindController {
    @Autowired
    private ExchangeWalletService exchangeWalletService;

    @ApiOperation("根据条件检索交易所绑定关系")
    @PostMapping("findExchangeWalletBindState")
    public ResponseResult findExchangeWalletBindState(@RequestBody FindExchangeBindStateReq findExchangeBindStateReq){
       return exchangeWalletService.findExchangeWalletBindState(findExchangeBindStateReq);//调用service 去执行查询绑定关系的操作
    }

    @ApiOperation("查询所有记录")
    @PostMapping("queryAllRecord")
    public ResponseResult queryAllRecord(@RequestBody FindExchangeBindStateReq findExchangeBindStateReq){
        return exchangeWalletService.queryAllRecord(findExchangeBindStateReq);//调用service 去执行查询绑定关系的操作
    }
}
