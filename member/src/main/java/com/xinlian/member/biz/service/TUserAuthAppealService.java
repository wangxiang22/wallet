package com.xinlian.member.biz.service;

import com.xinlian.common.request.UserAuthAppealReq;
import com.xinlian.common.response.ResponseResult;

public interface TUserAuthAppealService {
    /**
     * 查看用户是否符合提交申诉条件
     * @param uid 用户id
     * @return
     */
    ResponseResult findAppealStatus(Long uid);
    /**
     * 提交实名申诉
     * @param userAuthAppealReq
     * @return
     */
    ResponseResult insertAppeal(UserAuthAppealReq userAuthAppealReq);

    /**
     * look is user auth pass by uid
     * true 未实名
     * false 已实名
     * @param uid
     * @return
     */
    boolean queryAuthStatusByUid(Long uid);
}
