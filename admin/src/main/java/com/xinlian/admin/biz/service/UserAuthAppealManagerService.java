package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TUserAuthAppeal;
import com.xinlian.common.request.UserAuthAppealManagerReq;
import com.xinlian.common.request.UserAuthAppealSubmitReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface UserAuthAppealManagerService {
    /**
     * 根据搜索条件查询实名申诉信息（分页）
     * @param userAuthAppealManagerReq
     * @return
     */
    PageResult<List<TUserAuthAppeal>> queryAppealListPage(UserAuthAppealManagerReq userAuthAppealManagerReq);

    /**
     * 实名申诉审批操作
     * @param userAuthAppealSubmitReq
     * @return
     */
    ResponseResult updateAppealStatus(UserAuthAppealSubmitReq userAuthAppealSubmitReq);
}
