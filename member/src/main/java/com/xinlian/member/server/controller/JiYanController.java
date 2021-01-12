package com.xinlian.member.server.controller;

import com.xinlian.common.request.GeetestVo;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.JiYanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api("极验")
@RequestMapping("/{versionPath}/gt")
public class JiYanController {
    @Autowired
    private JiYanService jiYanService;
    /**
     * 初始化极验
     * @return
     */
    @PassToken
    @GetMapping("/register")
    @ApiOperation("初始化极验")
    public ResponseResult register(GeetestVo geetestVo)  {
        return jiYanService.register(geetestVo);
    }
    /**
     * 使用post方式，返回验证结果, request表单中必须包含challenge, validate, seccode
     * @return
     */
    @PassToken
    @ApiOperation("返回验证结果")
    @PostMapping("/validate")
    public ResponseResult validate(@RequestBody GeetestVo geetestVo) {
        return jiYanService.validate(geetestVo);
    }

}