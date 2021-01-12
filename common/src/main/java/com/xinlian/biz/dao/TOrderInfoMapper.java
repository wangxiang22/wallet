package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TOrderInfo;
import com.xinlian.common.dto.OrderInfoRealTimeDto;
import com.xinlian.common.dto.OrderInfoTotalAmountDto;
import com.xinlian.common.request.OrderInfoRealTimePageReq;
import com.xinlian.common.request.OrderInfoTotalAmountReq;
import com.xinlian.common.request.QuerySpotInfoReq;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
@Repository
public interface TOrderInfoMapper extends BaseMapper<TOrderInfo> {

    List<TOrderInfo> querySpotInfo(QuerySpotInfoReq querySpotInfo);

    int querySpotInfoCount(QuerySpotInfoReq querySpotInfo);

    List<TOrderInfo> exportSpotInfo(QuerySpotInfoReq querySpotInfo);

    /**
     * 分页查询今日实时订单明细/历史账单明细
     * @param pageReq
     * @return
     */
    List<OrderInfoRealTimeDto> findRealTimeOrderInfoPage(OrderInfoRealTimePageReq pageReq);

    /**
     * 查询买卖家总出入金
     * @param req
     * @return
     */
    OrderInfoTotalAmountDto findSellerBuyerAltogether(OrderInfoTotalAmountReq req);
}
