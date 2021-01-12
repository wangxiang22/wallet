package com.xinlian.member.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinlian.biz.model.TMiningApply;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TMiningApplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 挖矿申请表 前端控制器
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-25
 */
@RestController
@RequestMapping("/{versionPath}/tMiningApply")
@Api("挖矿申请")
public class TMiningApplyController {
	@Autowired
	private TMiningApplyService tMiningApplyService;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private JwtUtil jwtUtil;

	@ApiOperation("挖矿申请")
	@PostMapping("mingingApply")
	public ResponseResult mingingApply(@RequestBody TMiningApply tMiningApply) {
		if (null == tMiningApply.getUid()) {
			Long userIdL = jwtUtil.getUserId(request);
			if (null != userIdL) {
				tMiningApply.setUid(userIdL.intValue());
			}
		}
		if (null == tMiningApply.getNodeId()) {
			Long nodeIdL = jwtUtil.getNodeId(request);
			if (null != nodeIdL) {
				tMiningApply.setNodeId(nodeIdL.intValue());
			}
		}

		tMiningApplyService.mingingApply(tMiningApply);
		return ResponseResult.ok();
	}

	@ApiOperation("查询用户当前申请状态")
	@PostMapping("findUserApplyState")
	public ResponseResult findUserApplyState() {
		TMiningApply tMiningApply = tMiningApplyService.findUserApplyState();
		return ResponseResult.ok(tMiningApply);
	}

}
