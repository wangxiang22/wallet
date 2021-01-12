package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.TNewsTypeService;
import com.xinlian.biz.model.TNewsType;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * banner图表 前端控制器
 * </p>
 *
 * @author wjf
 * @since 2020-03-03
 */
@RestController
@RequestMapping("/tNewsType")
public class TNewsTypeController {

    @Autowired
    private TNewsTypeService tNewsTypeService;

    @ApiOperation("查询全部分类")
    @PostMapping("queryAll")
    public ResponseResult queryOne() {
        return tNewsTypeService.queryAll();
    }

    @ApiOperation("删除分类")
    @PostMapping("deleteById")
    public ResponseResult deleteById(@RequestBody TNewsType tNewsType) {
        return tNewsTypeService.deleteById(tNewsType.getId());
    }

    @ApiOperation("添加分类")
    @PostMapping("insertOne")
    public ResponseResult insertOne(@RequestBody TNewsType tNewsType) {
        return tNewsTypeService.insertOne(tNewsType);
    }

    @ApiOperation("修改分类")
    @PostMapping("updateNewsType")
    public ResponseResult updateNewsType(@RequestBody TNewsType tNewsType) {
        return tNewsTypeService.updateNewsType(tNewsType);
    }
}

