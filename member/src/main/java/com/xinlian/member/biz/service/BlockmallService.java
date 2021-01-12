package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.request.CatWalletPayReq;
import com.xinlian.common.request.CatWalletPayStatusReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.CatWalletPayCallbackRes;
import com.xinlian.common.response.ResponseResult;

public interface BlockmallService {
    ResponseResult<TUserInfo> cert(RegisterReq registerReq);

    void pay(CatWalletPayReq catWalletPayReq);

    ResponseResult<CatWalletPayCallbackRes> payStatus(CatWalletPayStatusReq catWalletPayStatusReq);

}