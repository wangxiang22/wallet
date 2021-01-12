package com.xinlian.member.biz.service;

import com.xinlian.biz.model.UserCurrencyStateReq;
import com.xinlian.common.request.ExchangeBalanceReq;
import com.xinlian.common.response.ResponseResult;

public interface ExchangeWalletWithdrawService {
     ResponseResult withdraw(UserCurrencyStateReq userCurrencyStateReq);

     ResponseResult getUserCurrencyState(UserCurrencyStateReq userCurrencyStateReq);

     ResponseResult getCurrencyBalance(UserCurrencyStateReq userCurrencyStateReq);

    ResponseResult withdrawHistory(Long userId);

    ResponseResult getExchangeBalance(ExchangeBalanceReq exchangeBalanceReq);
}
