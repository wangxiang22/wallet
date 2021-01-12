package com.xinlian.member.biz.service;

import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.UserMessageRes;

import java.util.List;

public interface TUserMessage {
    ResponseResult <List<UserMessageRes>> queryUserName(RegisterReq registerReq);

    ResponseResult sendQuerySms(RegisterReq req);

}
