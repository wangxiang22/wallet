package com.xinlian.member.server.controller;

import com.xinlian.common.request.UserAuthAppealReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TUserAuthAppealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 实名申诉接口
 */
@Api(value = "实名申诉接口")
@RestController
@RequestMapping("/{versionPath}/userAuthAppeal")
public class UserAuthAppealController {

    @Autowired
    private TUserAuthAppealService userAuthAppealService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "提交实名申诉",httpMethod = "POST")
    @PostMapping("/insertAppeal")
    public ResponseResult insertAppeal(@RequestBody UserAuthAppealReq userAuthAppealReq) {
        userAuthAppealReq.setUid(jwtUtil.getUserId(request));
        userAuthAppealReq.setNodeId(jwtUtil.getNodeId(request));
        return userAuthAppealService.insertAppeal(userAuthAppealReq);
    }

    @ApiOperation(value = "查看用户是否符合提交申诉条件",httpMethod = "POST")
    @PostMapping("/findAppealStatus")
    public ResponseResult findAppealStatus() {
        return userAuthAppealService.findAppealStatus(jwtUtil.getUserId(request));
    }
}
