package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TSmartContractHistoryBill;
import com.xinlian.common.request.SmartContractHistoryBillPageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.SmartContractHistoryBillRes;
import com.xinlian.common.response.SmartContractTotalRes;

import java.util.List;

/**
 * <p>
 * 智能合约历史账单管理 服务类
 * </p>
 *
 * @author lt
 * @since 2020-06-18
 */
public interface SmartContractHistoryBillService extends IService<TSmartContractHistoryBill> {

    /**
     * 根据账单日获取对应model
     * @param billDate
     * @return
     */
    TSmartContractHistoryBill getByBillDate(String billDate);

    /**
     * 在两个时间戳内完成的智能合约订单集合
     * @param startTime
     * @param endTime
     * @return
     */
    TSmartContractHistoryBill completeSmartContractOrders(Long startTime, Long endTime);
    /**
     * 分页查询智能合约历史账单
     * @param req
     * @return
     */
    PageResult<List<SmartContractHistoryBillRes>> findHistoryBillPage(SmartContractHistoryBillPageReq req);

    /**
     * 查询智能合约历史账单（不分页）
     * @param req
     * @return
     */
    ResponseResult<List<SmartContractHistoryBillRes>> findHistoryBill(SmartContractHistoryBillPageReq req);

    /**
     * 查询买卖家总出入金及出入金差额
     * @return
     */
    ResponseResult<SmartContractTotalRes> findTotalOutInAmount();

    /**
     * 智能合约数据统计
     * @param isForceRefresh
     * @param dimensionsType
     * @return
     */
    Object dataAnalysis(boolean isForceRefresh, String dimensionsType);

    /**
     * 智能合约-均价统计
     * @return
     */
    TOrder usdtSoldPrice();
}
