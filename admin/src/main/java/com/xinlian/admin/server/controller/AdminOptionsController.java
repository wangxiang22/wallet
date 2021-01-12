package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.AdminOptionsService;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.request.AdminOptionsReq;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 配置项信息管理接口
 */
@Api(value = "配置项信息管理接口")
@RestController
@RequestMapping(value = "/adminOptions")
public class AdminOptionsController {

    @Autowired
    private AdminOptionsService adminOptionsService;

    @ApiOperation(value = "查询全部配置项信息（分页）",httpMethod = "POST")
    @PostMapping("/findOptionsListPage")
    public PageResult<List<AdminOptions>> findOptionsListPage(@RequestBody PageReq pageReq) {
        return adminOptionsService.findOptionsListPage(pageReq);
    }

    @ApiOperation(value = "根据配置项id查询",httpMethod = "POST")
    @PostMapping("/findOptionById")
    public ResponseResult findOptionById(@RequestBody AdminOptionsReq adminOptionsReq) {
        return adminOptionsService.findOptionById(adminOptionsReq);
    }

    @ApiOperation(value = "新增配置项",httpMethod = "POST")
    @PostMapping("/createOption")
    public ResponseResult createOption(@RequestBody AdminOptions adminOptions) {
        return adminOptionsService.createOption(adminOptions);
    }

    @ApiOperation(value = "修改配置项",httpMethod = "POST")
    @PostMapping("/updateOption")
    public ResponseResult updateOption(@RequestBody AdminOptions adminOptions) {
        return adminOptionsService.updateOption(adminOptions);
    }

    @ApiOperation(value = "根据配置项id删除",httpMethod = "POST")
    @PostMapping("/deleteOption")
    public ResponseResult deleteOption(@RequestBody AdminOptionsReq adminOptionsReq) {
        return adminOptionsService.deleteOption(adminOptionsReq);
    }

}
