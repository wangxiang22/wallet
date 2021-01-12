package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TMiningApply;
import com.xinlian.common.request.FindAllUserReq;
import com.xinlian.common.request.MiningConfigReq;
import com.xinlian.common.response.ResponseResult;

public interface AdminMiningApplyService {
    void setConfig(MiningConfigReq miningConfigReq);

    void passUser(TMiningApply tMiningApply);

    ResponseResult findAllUser(FindAllUserReq findAllUserReq);
}
