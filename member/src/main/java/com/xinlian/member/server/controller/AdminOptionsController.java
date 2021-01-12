package com.xinlian.member.server.controller;


import com.xinlian.common.response.BootScreenRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.BootScreenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 配置项接口
 */
@Api(value = "配置项接口")
@RestController
@RequestMapping("/{versionPath}/adminOptions")
public class AdminOptionsController {
    @Autowired
    private BootScreenService bootScreenService;

    @ApiOperation(value = "查找app启动界面url及开关状态",httpMethod = "POST")
    @PostMapping("/findBootScreen")
    @PassToken
    public ResponseResult<BootScreenRes> findBootScreen() {
        return bootScreenService.findBootScreen();
    }
}

