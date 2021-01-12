package com.xinlian.admin.biz.service;

import com.xinlian.common.request.LoginReq;
import com.xinlian.common.request.SendEmailRequest;
import com.xinlian.common.response.AdminUserRes;
import com.xinlian.common.response.ResponseResult;

public interface LoginService {

    /**
     *
     * @param req
     * @return
     */
    ResponseResult<AdminUserRes> login(LoginReq req);


    /**
     * 发送邮箱code
     * @param req req
     * @return
     */
    void sendEmailCode(SendEmailRequest req);
}
