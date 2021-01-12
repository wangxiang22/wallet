package com.xinlian.biz.dao;

import com.xinlian.biz.model.TUserWithdrawAddressRef;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Component;


@Component
public interface TUserWithdrawAddressRefMapper extends BaseMapper<TUserWithdrawAddressRef> {

    TUserWithdrawAddressRef getByCriteria(TUserWithdrawAddressRef tUserWithdrawAddressRef);
}
