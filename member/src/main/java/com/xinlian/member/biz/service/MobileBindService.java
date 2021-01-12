package com.xinlian.member.biz.service;

import com.xinlian.common.request.BindMobileReq;
import com.xinlian.common.response.ResponseResult;

public interface MobileBindService {
    /**
     * 判断手机号码是否符合绑定要求（根据节点表手机注册数量来判断节点内手机绑定数量的限制）
     * @param bindMobileReq
     * @return
     */
    ResponseResult findMobileExists(BindMobileReq bindMobileReq);

    /**
     * 绑定手机号
     * @param bindMobileReq
     * @return
     */
    ResponseResult bindMobile(BindMobileReq bindMobileReq);

    /**
     * 先校验是否超过修改次数，再发送手机验证码用于修改国家区号
     * @param bindMobileReq
     * @return
     */
    ResponseResult sendMobileSms(BindMobileReq bindMobileReq);

    /**
     * 修改手机区号
     * @param bindMobileReq
     * @return
     */
    ResponseResult updateCountryCode(BindMobileReq bindMobileReq);
}
