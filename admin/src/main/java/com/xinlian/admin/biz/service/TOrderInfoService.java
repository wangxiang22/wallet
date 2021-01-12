package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TOrderInfo;
import com.xinlian.common.request.OrderInfoRealTimePageReq;
import com.xinlian.common.request.OrderInfoTotalAmountReq;
import com.xinlian.common.request.QuerySpotInfoReq;
import com.xinlian.common.response.OrderInfoRealTimeRes;
import com.xinlian.common.response.OrderInfoTotalAmountRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-19
 */
public interface TOrderInfoService extends IService<TOrderInfo> {

    ResponseResult querySpotInfo(QuerySpotInfoReq querySpotInfo);

    ResponseResult queryEveryOrderList(QuerySpotInfoReq querySpotInfo);

    ResponseResult queryBuyerInfoByAddr(String addr);

    ResponseResult exportSpotInfo(QuerySpotInfoReq querySpotInfo, HttpServletResponse httpServletResponse);

    ResponseResult exportQueryEveryOrderList(QuerySpotInfoReq querySpotInfo);

    /**
     * 分页查询今日实时订单明细/历史账单明细
     * @param req
     * @return
     */
    PageResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfoPage(OrderInfoRealTimePageReq req);

    /**
     * 查询今日实时订单明细/历史账单明细（不分页）
     * @param req
     * @return
     */
    ResponseResult<List<OrderInfoRealTimeRes>> findRealTimeOrderInfo(OrderInfoRealTimePageReq req);

    /**
     * 查询买卖家总出入金及出入金差额
     * @param req
     * @return
     */
    ResponseResult<OrderInfoTotalAmountRes> findSellerBuyerAltogether(OrderInfoTotalAmountReq req);
}
