package com.xinlian.admin.biz.service;

import com.xinlian.common.request.RefuseReq;
import com.xinlian.common.request.UserAuthQueryReq;
import com.xinlian.common.request.UserAuthUpdateReq;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface UserAuthManagerService {

    ResponseResult queryAll(UserAuthQueryReq userAuthQueryReq)throws Exception;

    ResponseResult takeOffer(List<Long> uids);

    ResponseResult refuse(List<Long> uids);

    ResponseResult updateByUid(UserAuthUpdateReq userAuthUpdateReq);

    ResponseResult refuseOne(RefuseReq refuseReq);

    /**
     * 重新过滤国内通道已拒绝的实名信息（20200814中午十二点 - 20200819晚上23:59:59的数据）
     */
    void batchAuditByBaiduRecognition();
}
