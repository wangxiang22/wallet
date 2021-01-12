package com.xinlian.member.server.controller;

import com.xinlian.common.request.PledgeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.PledgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 矿池质押相关接口
 */
@Api(value = "矿池质押相关接口")
@RestController
@RequestMapping("/{versionPath}/pledge")
public class PledgeController {

    @Autowired
    private PledgeService pledgeService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;

    @ApiOperation(value = "查询矿池质押金额及币种",httpMethod = "POST")
    @PostMapping("/findPledgeAmountCurrency")
    public ResponseResult findPledgeAmountCurrency() {
        Long nodeId = jwtUtil.getNodeId(request);
        Long userId = jwtUtil.getUserId(request);
        return pledgeService.findPledgeAmountCurrency(userId,nodeId);
    }

    @ApiOperation(value = "提交质押申请",httpMethod = "POST")
    @PostMapping("/submitPledgeApply")
    public ResponseResult submitPledgeApply(@RequestBody PledgeReq pledgeReq) {
        pledgeReq.setUid(jwtUtil.getUserId(request));
        pledgeReq.setNodeId(jwtUtil.getNodeId(request));
        return pledgeService.submitPledgeApply(pledgeReq);
    }
}
