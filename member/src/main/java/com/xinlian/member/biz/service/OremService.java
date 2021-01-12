package com.xinlian.member.biz.service;

import com.xinlian.common.response.ResponseResult;
import com.xinlian.biz.model.TUserInfo;

public interface OremService {
    ResponseResult activateOrem(TUserInfo tUserInfo);

    ResponseResult isOremActivate(TUserInfo tUserInfo);
}
