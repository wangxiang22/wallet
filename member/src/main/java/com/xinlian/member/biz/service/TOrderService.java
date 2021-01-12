package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TOrder;
import com.baomidou.mybatisplus.service.IService;
import com.xinlian.common.request.OrderStateReq;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lx
 * @since 2020-06-06
 */
public interface TOrderService extends IService<TOrder> {

    TOrder queryOneByUid(OrderStateReq orderStateReq);

    int updateByOldState(TOrder tOrder);
}
