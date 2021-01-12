package com.xinlian.member.biz.service;

import com.xinlian.common.request.AddressReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.biz.model.TAddressPool;

import java.util.List;

public interface IAddressPoolService {
    /**
     * 状态 查询地址
     * @param status
     * @return
     */
    ResponseResult<List<TAddressPool>> findTAddressPool(Integer status);

    PageResult<List<TAddressPool>> findTAddressPoolPage(AddressReq req);

    TAddressPool undistributed();

    int updateModel(TAddressPool addressPool);

    int addAddressTools(TAddressPool tAddressPool);

    /**
     * 获取定时任务地址池已经有多少数值
     * @return
     */
    int getBatchCount();
}
