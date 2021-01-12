package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TWithdrawTradeSuccessLog;
import com.xinlian.common.request.WithdrawTradeSuccessLogReq;
import com.xinlian.common.request.WithdrawTradeSuccessTriggerReq;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 账户大额变动流水记录表 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-08-13
 */
@Component
public interface TWithdrawTradeSuccessLogMapper extends BaseMapper<TWithdrawTradeSuccessLog> {

    /**
     * 分页条件查询账户大额变动流水记录
     * @param req 分页查询条件
     * @return 流水记录
     */
    List<TWithdrawTradeSuccessLog> queryTradeSuccessLogList(WithdrawTradeSuccessLogReq req);

    /**
     * 条件查询账户大额变动流水记录总数
     * @param req 查询条件
     * @return 记录总数
     */
    Long queryTradeSuccessLogListCount(WithdrawTradeSuccessLogReq req);

    /**
     * 查询账户大额变动流水中异常金额的列表
     * @param req 查询参数
     * @return 查询结果列表
     */
    List<TWithdrawTradeSuccessLog> queryTradeSuccessTriggerList(WithdrawTradeSuccessTriggerReq req);

}
