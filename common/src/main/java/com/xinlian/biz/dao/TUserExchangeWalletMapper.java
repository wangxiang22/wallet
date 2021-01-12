package com.xinlian.biz.dao;

import com.xinlian.biz.model.TRocketBind;
import com.xinlian.biz.model.TUserExchangeWallet;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.common.request.FindExchangeBindStateReq;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TUserExchangeWalletMapper extends BaseMapper<TUserExchangeWallet> {

    List<TRocketBind> findExchangeWalletBindState(FindExchangeBindStateReq findExchangeBindStateReq);

    List<TRocketBind> queryAllRecord(FindExchangeBindStateReq findExchangeBindStateReq);

    int queryAllRecordCount(FindExchangeBindStateReq findExchangeBindStateReq);

    Integer queryCount(FindExchangeBindStateReq findExchangeBindStateReq);
}
