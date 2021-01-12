package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TSmartContractHistoryBill;
import com.xinlian.common.dto.SmartContractTotalDto;
import com.xinlian.common.request.SmartContractHistoryBillPageReq;
import com.xinlian.common.response.SmartContractHisBillResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 智能合约历史账单 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-06-18
 */
@Component
public interface TSmartContractHistoryBillMapper extends BaseMapper<TSmartContractHistoryBill> {
    /**
     * 分页查询智能合约历史账单
     * @param req
     * @return
     */
    List<TSmartContractHistoryBill> findHistoryBillPage(SmartContractHistoryBillPageReq req);

    /**
     * 查询买卖家总出入金
     * @param today 账期参数
     * @return
     */
    SmartContractTotalDto findSellerBuyerTotalAmount(String today);

    /**
     * 根据账单日获取对应model
     * @param billDate
     * @return
     */
    TSmartContractHistoryBill getByBillDate(String billDate);

    List<SmartContractHisBillResponse> statisticsSmartContractHistBill(String dimensionsType, String firstDayOfWeekTimeStr, String lastDayOfWeekTimeStr);
}
