package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TSmartContractHistoryBill;
import com.xinlian.common.request.OrderStateReq;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.SmartContractHisBillResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lx
 * @since 2020-06-06
 */
@Repository
public interface TOrderMapper extends BaseMapper<TOrder> {

    TOrder queryOneByUid(OrderStateReq orderStateReq);

    List<TOrder> queryOutTimeOrders(@Param("orderTimeOutL") Long orderTimeOutL, @Param("nowTime") Long nowTime);

    BigDecimal checkUserTodaySellAmount(@Param("todayStartTime") Long todayStartTime,@Param("todayEndTime") Long todayEndTime,@Param("uid") Long uid);

    List<TOrder> queryEveryOrderList(QuerySpotInfoReq querySpotInfo);

    Integer queryEveryOrderListCount(QuerySpotInfoReq querySpotInfo);

    List<TOrder> exportQueryEveryOrderList(QuerySpotInfoReq querySpotInfo);

    TSmartContractHistoryBill completeSmartContractOrders(@Param(value = "startTime") Long startTime,
                                                          @Param(value = "endTime") Long endTime);

    TOrder usdtSoldPrice();

    /**
     * 统计某个时间内 usdt单价
     * @param startTime
     * @param endTime
     * @return
     */
    List<SmartContractHisBillResponse> smartContractUsdtPriceInTime(@Param(value = "startTime") Long startTime,
                                                                    @Param(value = "endTime") Long endTime);


}
