package com.xinlian.member.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.request.UserAuthenticationReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.TPayPwdRes;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IUserAuthenticationService;
import com.xinlian.member.biz.service.TPayPwdChangeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("用户实名")
@RequestMapping("/{versionPath}/auth")
@RestController
public class UserAuthenticationController {

	@Autowired
	private IUserAuthenticationService iUserAuthenticationService;
	@Autowired
	private TPayPwdChangeService tPayPwdChangeService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private HttpServletRequest httpServletRequest;

	@ApiOperation("实名认证接口")
	@PostMapping("toAuth")
	public ResponseResult toAuth(@RequestBody UserAuthenticationReq userAuthenticationReq) {
		if (null == userAuthenticationReq.getNode()) {
			userAuthenticationReq.setNode(jwtUtil.getNodeId(httpServletRequest));
		}

		return iUserAuthenticationService.toAuth(userAuthenticationReq, httpServletRequest);
	}

	@ApiOperation("实名认证状态查询")
	@PostMapping("getAuthState")
	public ResponseResult getAuthState() {
		// 根据uid获取用户信息
		Long userId = jwtUtil.getUserId(httpServletRequest);
		return iUserAuthenticationService.getAuthState(userId);
	}

	@ApiOperation("忘记支付密码验证本人身份(手机号无法使用)")
	@PostMapping("checkUserAuth")
	public ResponseResult checkUserAuth(@RequestBody CheckUserAuthReq checkUserAuthReq) {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		checkUserAuthReq.setUid(userId);
		return iUserAuthenticationService.checkUserAuth(checkUserAuthReq);
	}

	@ApiOperation("忘记支付密码(手机号无法使用)")
	@PostMapping("forgetPayPwd")
	public ResponseResult forgetPayPwd(@RequestBody CheckUserAuthReq checkUserAuthReq) {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		checkUserAuthReq.setUid(userId);
		return iUserAuthenticationService.forgetPayPwd(checkUserAuthReq);
	}

	@ApiOperation("查询忘记密码申请状态")
	@PostMapping("queryForgetPwdState")
	public ResponseResult queryForgetPwdState() {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		TPayPwdChange tPayPwdChange = tPayPwdChangeService.queryState(userId);
//        TPayPwdChange tPayPwdChange = tPayPwdChangeService
//                .selectOne(new EntityWrapper<TPayPwdChange>()
//                        .eq("uid", userId)
//                        .orderBy("id",false));
		if (tPayPwdChange != null) {
			TPayPwdRes tPayPwdRes = new TPayPwdRes();
			tPayPwdRes.setRemark(tPayPwdChange.getRemark());
			tPayPwdRes.setState(tPayPwdChange.getState());
			return new ResponseResult(tPayPwdRes);
		}
		return ResponseResult.ok();
	}
}
