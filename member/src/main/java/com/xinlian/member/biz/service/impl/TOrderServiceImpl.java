package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.dao.TOrderMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.common.request.OrderStateReq;
import com.xinlian.member.biz.service.TOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lx
 * @since 2020-06-06
 */
@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder> implements TOrderService {

    @Autowired
    private TOrderMapper tOrderMapper;

    @Override
    public TOrder queryOneByUid(OrderStateReq orderStateReq) {
        return tOrderMapper.queryOneByUid(orderStateReq);
    }

    @Override
    public int updateByOldState(TOrder tOrder) {
        return 0;
    }


}
