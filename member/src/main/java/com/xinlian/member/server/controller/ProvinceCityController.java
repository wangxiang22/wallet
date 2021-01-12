package com.xinlian.member.server.controller;

import com.xinlian.common.response.ProvinceRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.TProvinceCityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 省市信息接口
 */
@Api(value = "省市信息接口")
@RestController
@RequestMapping("/{versionPath}/provinceCity")
public class ProvinceCityController {
    @Autowired
    private TProvinceCityService provinceCityService;

    @ApiOperation(value = "查询省市组合信息",httpMethod = "POST")
    @PostMapping("/findProvinceCityByNodeId")
    @PassToken
    public ResponseResult<List<ProvinceRes>> findProvinceCityByNodeId(@ApiParam(name = "nodeId",value = "节点id",required = true)
                                                                      @RequestParam Long nodeId) {
        return provinceCityService.findProvinceCityByNodeId(nodeId);
    }
}
