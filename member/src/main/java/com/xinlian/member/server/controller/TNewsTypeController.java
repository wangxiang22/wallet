package com.xinlian.member.server.controller;


import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.service.TNewsTypeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * banner图表 前端控制器
 * </p>
 *
 * @author wx
 * @since 2020-03-04
 */
@RestController
@RequestMapping("/{versionPath}/tNewsType")
public class TNewsTypeController {

    @Autowired
    private TNewsTypeService tNewsTypeService;

    @ApiOperation("查询新闻分类")
    @PostMapping("queryNewsType")
    public ResponseResult queryOne() {
        return tNewsTypeService.queryAll();
    }

}

