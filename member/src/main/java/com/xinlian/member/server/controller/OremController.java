package com.xinlian.member.server.controller;


import com.xinlian.common.request.ActivateOremReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.member.biz.service.OremService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Api("矿机")
@RestController
@RequestMapping("/{versionPath}/orem")
public class OremController {

    @Autowired
    private OremService oremService;
    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation("激活矿机")
    @PostMapping("activateOrem")
    public ResponseResult activateOrem(@RequestBody ActivateOremReq activateOremReq, HttpServletRequest httpServletRequest){
        TUserInfo tUserInfo=new TUserInfo();
        tUserInfo.setPayPassWord(activateOremReq.getPassword());
        tUserInfo.setUid(jwtUtil.getUserId(httpServletRequest));
        return oremService.activateOrem(tUserInfo);
    }
    @ApiOperation("是否激活矿机")
    @PostMapping("isOremActivate")
    public ResponseResult isOremActivate(HttpServletRequest httpServletRequest){
        TUserInfo tUserInfo=new TUserInfo();
        tUserInfo.setUid(jwtUtil.getUserId(httpServletRequest));
        return oremService.isOremActivate(tUserInfo);
    }
}
