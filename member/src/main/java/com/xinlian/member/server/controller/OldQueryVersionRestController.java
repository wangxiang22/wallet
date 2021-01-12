package com.xinlian.member.server.controller;


import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.TUpdateVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "旧版本获取版本")
@RestController
@Slf4j
public class OldQueryVersionRestController {

    @Autowired
    TUpdateVersionService updateVersionService;

    @ApiOperation("获取新版本")
    @PostMapping("/tUpdateVersion/queryVersion")
    @PassToken
    public ResponseResult queryVersion(@RequestBody VersionDataReq versionReq){
        return updateVersionService.queryVersion(versionReq);
    }

}
