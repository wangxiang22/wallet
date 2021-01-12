package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TWithdrawTradeSuccessLog;
import com.xinlian.common.request.WithdrawTradeSuccessLogReq;
import com.xinlian.common.request.WithdrawTradeSuccessTriggerReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface WithdrawTradeSuccessLogService {
    /**
     * 分页查询账户大额变动流水记录表
     * @param req 查询参数
     * @return 分页查询结果
     */
    PageResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessLogListPage(WithdrawTradeSuccessLogReq req);

    /**
     * 查询账户大额变动流水中异常金额的列表
     * @param req 查询参数
     * @return 查询结果列表
     */
    ResponseResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessTriggerList(WithdrawTradeSuccessTriggerReq req);
}
