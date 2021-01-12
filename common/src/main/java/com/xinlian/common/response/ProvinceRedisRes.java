package com.xinlian.common.response;

import lombok.Data;

import java.util.List;

@Data
public class ProvinceRedisRes {
    private String provinceCode;//省份编码
    private String provinceName;//省份名称
    private String nodeId;//节点id
    private List<CityRes> cityResList;//省份下的城市列表
}
