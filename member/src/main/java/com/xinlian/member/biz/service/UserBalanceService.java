package com.xinlian.member.biz.service;


import com.github.pagehelper.PageInfo;
import com.xinlian.common.request.CurrencyInfoRes;
import com.xinlian.common.request.GetOneTradeInfoReq;
import com.xinlian.common.request.TradeInfoReq;
import com.xinlian.common.response.ResponseResult;

public interface UserBalanceService  {
    ResponseResult getAllCurrencyBalance(Long userId,Long nodeId);

    PageInfo getTradeInfo(TradeInfoReq tradeInfoReq);

    ResponseResult getOneTradeInfo(GetOneTradeInfoReq getOneTradeInfoReq);

    ResponseResult getCurrencyBalanceInfo(CurrencyInfoRes currencyInfoRes);
}
