package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.AdminVersionService;
import com.xinlian.biz.model.AdminUpdateVersionInfo;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(value = "版本管理")
@RestController
@RequestMapping("/version")
@Slf4j
public class AdminVersionController {

    @Autowired
    private AdminVersionService adminVersionService;


    @ApiOperation(value = "查询所有版本")
    @PostMapping("/queryVersion")
    public PageResult<List<TUpdateVersion>> queryVersion(@RequestBody PageReq pageReq){
       return adminVersionService.queryVersion(pageReq);
    }

    @ApiOperation(value = "查询此版本的下载，推送数据")
    @PostMapping("/queryVersionData")
    public PageResult<List<AdminUpdateVersionInfo>> queryVersion(@RequestBody VersionDataReq versionDataReq){
        return adminVersionService.queryVersionData(versionDataReq);
    }

    @ApiOperation(value = "新增版本")
    @PostMapping("/addVersion")
    public ResponseResult addVersion(@RequestBody TUpdateVersion tUpdateVersion) {
        return adminVersionService.addVersion(tUpdateVersion);
    }

    @ApiOperation(value = "修改版本")
    @PostMapping("/updateVersion")
    public ResponseResult updateVersion(@RequestBody TUpdateVersion tUpdateVersion){
        return adminVersionService.updateVersion(tUpdateVersion);
    }


    @ApiOperation(value = "删除版本")
    @PostMapping("/deleteVersion")
    public ResponseResult deleteVersion(@RequestBody VersionDataReq versionReq){
        return adminVersionService.deleteVersion(versionReq);
    }

    @ApiOperation(value = "查看最新版本信息")
    @PostMapping("/queryNewVersion")
    public ResponseResult queryNewVersion(@RequestBody VersionDataReq versionReq){
        return adminVersionService.queryNewVersion(versionReq);
    }

    @ApiOperation(value = "查看版本详细信息")
    @PostMapping("/queryVersionTime")
    public ResponseResult queryVersionTime(@RequestBody VersionDataReq versionDataReq){
        return adminVersionService.queryVersionTime(versionDataReq);
    }
}
