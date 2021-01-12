package com.xinlian.admin.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.common.request.*;
import com.xinlian.common.response.QueryOrderListRes;
import com.xinlian.common.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface TNewOrderService extends IService<TNewOrder> {

    /**
     * 下单
     * @param queryOrderListReq
     * @return
     */

    QueryOrderListRes queryOrderList(QueryOrderListReq queryOrderListReq);

    /**
     * 导出excel
     * @param orderExportReq
     * @param request
     * @param response
     */
    void exportOrders(OrderExportReq orderExportReq, HttpServletRequest request, HttpServletResponse response);

    /**
     * 发货
     * @param orderExportReq
     */
    void deliver(OrderSendReq orderExportReq);

    /**
     * 获取物流信息
     * @param orderId
     * @return
     */
    JSONObject queryExpress(Long orderId);

    /**
     * 修改订单收货相关信息
     * @param req 条件及修改参数
     * @return 修改结果
     */
    void updateNewOrderById(UpdateNewOrderReq req);
}
