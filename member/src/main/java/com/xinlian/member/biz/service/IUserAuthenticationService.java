package com.xinlian.member.biz.service;

import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.request.UserAuthenticationReq;
import com.xinlian.common.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;

public interface IUserAuthenticationService {
    ResponseResult toAuth(UserAuthenticationReq userAuthenticationReq, HttpServletRequest httpServletRequest);

    ResponseResult getAuthState(Long uid);

    ResponseResult checkUserAuth(CheckUserAuthReq checkUserAuthReq);

    ResponseResult forgetPayPwd(CheckUserAuthReq checkUserAuthReq);
}
