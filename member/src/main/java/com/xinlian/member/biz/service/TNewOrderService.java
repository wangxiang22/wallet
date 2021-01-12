package com.xinlian.member.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.common.request.BeforeSendReq;
import com.xinlian.common.request.OrderListReq;
import com.xinlian.common.request.OrderReq;
import com.xinlian.common.response.ResponseResult;

public interface TNewOrderService extends IService<TNewOrder> {

    /**
     * 下单
     * @param orderReq
     * @return
     */
    ResponseResult order(OrderReq orderReq);

    /**
     * 发送验证码
     * @param uid
     * @param nodeId
     * @param beforeSendReq
     * @return
     */
    ResponseResult sendOrderCode(Long uid, Long nodeId, BeforeSendReq beforeSendReq);

    /**
     * 获得订单列表
     * @param uid
     * @param orderListReq
     * @return
     */
    ResponseResult list(Long uid, OrderListReq orderListReq);

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    ResponseResult orderDetail(Long orderId);

    /**
     * 从配置项获得价格
     * @return
     */
    ResponseResult goodsPrice();

}
