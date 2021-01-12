package com.xinlian.member.server.controller;

import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.NodeCurrencyReq;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IServerNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 节点信息接口
 */
@Api(value = "节点信息接口")
@RestController
@RequestMapping("/{versionPath}/serverNode")
public class ServerNodeController {

    @Autowired
    private IServerNodeService serverNodeService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "查询所有节点信息",httpMethod = "GET")
    @GetMapping("/findNodeListByStatus")
    @PassToken
    public ResponseResult findNodeListByStatus(@RequestParam(name = "node_type", required = false) Integer node_type,
                                               @RequestParam(name = "parentId", required = false) Long parentId,
                                               @RequestParam(name = "id", required = false) Long id) {
        try {
            if (null != node_type) {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).msg("请下载最新APP！！！").build();
            }
            return serverNodeService.findNodeListByStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL).build();
        }
    }

    @ApiOperation(value = "获取token查询用户的节点信息",httpMethod = "POST")
    @PostMapping("/findUserNode")
    public ResponseResult findUserNode() {
        return serverNodeService.findUserNode(jwtUtil.getNodeId(request));
    }

    @ApiOperation(value = "根据用户节点id及币种id查找该币种的充提状态",httpMethod = "POST")
    @PostMapping("/findRechargeAndCashStatus")
    public ResponseResult findRechargeAndCashStatus(@RequestBody NodeCurrencyReq nodeCurrencyReq) {
        nodeCurrencyReq.setUid(jwtUtil.getUserId(request));
        nodeCurrencyReq.setNodeId(jwtUtil.getNodeId(request));
        return serverNodeService.findRechargeAndCashStatus(nodeCurrencyReq);
    }
}
