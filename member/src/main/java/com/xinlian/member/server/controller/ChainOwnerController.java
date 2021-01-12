package com.xinlian.member.server.controller;

import com.xinlian.common.request.ChainOwnerReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IChainOwnerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 链权人相关接口
 */
@Api(value = "链权人相关接口")
@RestController
@RequestMapping("/{versionPath}/chainOwner")
public class ChainOwnerController {

    @Autowired
    private IChainOwnerService chainOwnerService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "获取链权人相关实名信息",httpMethod = "POST")
    @PostMapping("/findChainOwnerUser")
    public ResponseResult findChainOwnerUser() {
        return chainOwnerService.findChainOwnerUser(jwtUtil.getUserId(request));
    }

    @ApiOperation(value = "上传链权人填写的相关信息||保存链权人证书url地址",httpMethod = "POST")
    @PostMapping("/updateChainOwnerUser")
    public ResponseResult updateChainOwnerUser(@RequestBody ChainOwnerReq chainOwnerReq) {
        chainOwnerReq.setUid(jwtUtil.getUserId(request));
        return chainOwnerService.updateChainOwnerUser(chainOwnerReq);
    }

    @ApiOperation(value = "查询链权人资产",httpMethod = "POST")
    @PostMapping("/findChainOwnerAsset")
    public ResponseResult findChainOwnerAsset() {
        return chainOwnerService.findChainOwnerAsset(jwtUtil.getUserId(request));
    }
}
