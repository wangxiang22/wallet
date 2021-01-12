package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.WithdrawTradeSuccessLogService;
import com.xinlian.biz.model.TWithdrawTradeSuccessLog;
import com.xinlian.common.request.WithdrawTradeSuccessLogReq;
import com.xinlian.common.request.WithdrawTradeSuccessTriggerReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "账户大额变动流水记录表管理接口")
@RestController
@RequestMapping(value = "/withdrawTradeSuccessLog")
@Slf4j
public class WithdrawTradeSuccessLogController {
    @Autowired
    private WithdrawTradeSuccessLogService withdrawTradeSuccessLogService;

    @ApiOperation(value = "分页查询账户大额变动流水记录表",httpMethod = "POST")
    @PostMapping("/queryTradeSuccessLogListPage")
    public PageResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessLogListPage(@RequestBody WithdrawTradeSuccessLogReq req) {
        return withdrawTradeSuccessLogService.queryTradeSuccessLogListPage(req);
    }

    @ApiOperation(value = "查询账户大额变动流水中异常金额的列表",httpMethod = "POST")
    @PostMapping("/queryTradeSuccessTriggerList")
    public ResponseResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessTriggerList(@RequestBody WithdrawTradeSuccessTriggerReq req) {
        return withdrawTradeSuccessLogService.queryTradeSuccessTriggerList(req);
    }
}
