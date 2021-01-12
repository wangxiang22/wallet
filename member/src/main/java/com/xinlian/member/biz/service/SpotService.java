package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.request.CheckPwdReq;
import com.xinlian.common.request.OrderStateReq;
import com.xinlian.common.request.SellCatReq;
import com.xinlian.common.request.SureBuyReq;
import com.xinlian.common.response.ResponseResult;

public interface SpotService {
    ResponseResult sellCat(SellCatReq sellCatReq);

    ResponseResult findOrderState(OrderStateReq orderStateReq);

    ResponseResult buyCat(SellCatReq sellCatReq);

    ResponseResult sureBuy(SureBuyReq sureBuyReq);

    ResponseResult checkPayPassword(CheckPwdReq checkPwdReq);

    ResponseResult findOrderRecord(Long uid);

    ResponseResult checkSellCode(SellCatReq sellCatReq);

    ResponseResult checkCanTrade(SellCatReq sellCatReq);
}
