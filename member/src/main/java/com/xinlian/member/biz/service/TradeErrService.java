package com.xinlian.member.biz.service;

import com.xinlian.biz.model.UserCurrencyStateReq;

public interface TradeErrService {
    void saveErrorLog(UserCurrencyStateReq userCurrencyStateReq);
}
