package com.xinlian.member.biz.service;

import com.xinlian.common.response.ProvinceRes;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface TProvinceCityService {
    /**
     * 查询省市组合信息
     * @param nodeId 节点id
     * @return 省市组合信息
     */
    ResponseResult<List<ProvinceRes>> findProvinceCityByNodeId(Long nodeId);
}
