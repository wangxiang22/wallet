package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.service.IService;
import com.xinlian.biz.model.TRocketBind;
import com.xinlian.biz.model.TVersion;
import com.xinlian.common.request.QueryBindInfoReq;
import com.xinlian.common.response.ResponseResult;

public interface BindExchangeInfoService extends IService<TRocketBind> {
    ResponseResult queryBindInfo(QueryBindInfoReq queryBindInfo);
}
