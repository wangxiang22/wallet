package com.xinlian.admin.biz.service;

import com.xinlian.common.request.FindExchangeBindStateReq;
import com.xinlian.common.response.ResponseResult;

public interface ExchangeWalletService {

    ResponseResult findExchangeWalletBindState(FindExchangeBindStateReq findExchangeBindStateReq);

    ResponseResult queryAllRecord(FindExchangeBindStateReq findExchangeBindStateReq);
}
