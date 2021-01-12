package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.BindMobileReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.jwt.annotate.EncryptionAnnotation;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.MobileBindService;
import com.xinlian.member.server.controller.handler.RegisterHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 钱包用户绑定手机号接口
 */
@Api(value = "钱包用户绑定手机号接口")
@RestController
@RequestMapping("/{versionPath}/mobileBind")
public class MobileBindController {
    @Autowired
    private MobileBindService mobileBindService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RegisterHandler registerHandler;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;

    @ApiOperation(value = "校验手机号是否符合绑定规则",httpMethod = "POST")
    @PostMapping("/findMobileExists")
    public ResponseResult findMobileExists(@RequestBody BindMobileReq bindMobileReq) {
        try {
            Long nodeId = jwtUtil.getNodeId(request);
            if(nodeVoyageUtil.belongVoyageNode(nodeId)) {
                if(86==bindMobileReq.getCountryCode()){
                    throw new BizException("请确定国家区号是否正确!");
                }
                registerHandler.judgeAbroadNodeIsRegister(nodeId, bindMobileReq.getMobile());
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
            }else {
                bindMobileReq.setUid(jwtUtil.getUserId(request));
                bindMobileReq.setNodeId(jwtUtil.getNodeId(request));
                return mobileBindService.findMobileExists(bindMobileReq);
            }
        }catch (BizException e){
            return new ResponseResult(e);
        }catch (Exception e){
            return new ResponseResult("校验手机号是否符合绑定规则异常，请稍后重试!");
        }
    }

    @ApiOperation(value = "绑定手机号",httpMethod = "POST")
    @PostMapping("/bindMobile")
    public ResponseResult bindMobile(@RequestBody BindMobileReq bindMobileReq) {
        try {
            Long nodeId = jwtUtil.getNodeId(request);
            if(nodeVoyageUtil.belongVoyageNode(nodeId)) {
                if(86==bindMobileReq.getCountryCode()){
                    throw new BizException("请确定国家区号是否正确!");
                }
                registerHandler.judgeAbroadNodeIsRegister(nodeId, bindMobileReq.getMobile());
            }
            bindMobileReq.setUid(jwtUtil.getUserId(request));
            return mobileBindService.bindMobile(bindMobileReq);
        }catch (Exception e){
            return new ResponseResult(new BizException("绑定手机号出现异常!"));
        }
    }

    @ApiOperation(value = "先校验是否超过修改次数，再发送手机验证码用于修改国家区号",httpMethod = "POST")
    @PostMapping("/sendMobileSms")
    @EncryptionAnnotation
    public ResponseResult sendMobileSms(@RequestBody Map<String,String> paramMap) {
        BindMobileReq bindMobileReq = JSONObject.parseObject(paramMap.get("data"),BindMobileReq.class);
        bindMobileReq.setNodeId(jwtUtil.getNodeId(request));
        return mobileBindService.sendMobileSms(bindMobileReq);
    }

    @ApiOperation(value = "修改手机区号",httpMethod = "POST")
    @PostMapping("/updateCountryCode")
    public ResponseResult updateCountryCode(@RequestBody BindMobileReq bindMobileReq) {
        Long nodeId = jwtUtil.getNodeId(request);
        if(nodeVoyageUtil.belongVoyageNode(nodeId)) {
            if(86==bindMobileReq.getCountryCode()){
                throw new BizException("请确定国家区号是否正确!");
            }
            registerHandler.judgeAbroadNodeIsRegister(nodeId, bindMobileReq.getMobile());
        }
        bindMobileReq.setUid(jwtUtil.getUserId(request));
        bindMobileReq.setNodeId(jwtUtil.getNodeId(request));
        return mobileBindService.updateCountryCode(bindMobileReq);
    }
}
