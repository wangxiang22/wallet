package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TNewOrder;
import com.xinlian.common.request.OrderExportReq;
import com.xinlian.common.request.QueryOrderListReq;
import com.xinlian.common.request.UpdateNewOrderReq;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TNewOrderMapper extends BaseMapper<TNewOrder> {

    List<TNewOrder> queryOrderList(QueryOrderListReq queryOrderListReq);

    Integer queryOrderCount(QueryOrderListReq queryOrderListReq);

    List<TNewOrder> exportOrders(OrderExportReq orderExportReq);

    int updateByIds(List<TNewOrder> list);

    /**
     * 修改订单收货相关信息
     * @param req 条件及修改参数
     * @return 修改结果
     */
    int updateNewOrderById(UpdateNewOrderReq req);
}
