package com.xinlian.common.response;

import lombok.Data;

import java.util.List;

@Data
public class ProvinceRes {
    private String provinceCode;//省份编码
    private String provinceName;//省份名称
    private List<CityRes> cityResList;//省份下的城市列表
}
