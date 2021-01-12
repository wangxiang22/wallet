package com.xinlian.admin.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.service.AdminUserService;
import com.xinlian.admin.biz.service.LoginService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.common.enums.OperationLogLevelEnum;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.LoginReq;
import com.xinlian.common.request.SendEmailRequest;
import com.xinlian.common.request.UpdateAdminUserReq;
import com.xinlian.common.response.AdminUserRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Api(value = "登录")
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

	@Autowired
	private LoginService loginService;
	@Autowired
	private AdminUserService adminUserService;

	@Autowired
	private JwtUtil jwtUtil;

	@RequestMapping(value = "/login1421Login", method = RequestMethod.POST)
	@ResponseBody
	@OpeAnnotation(typeName = OperationTypeEnum.SYSTEM_LOGIN, logLevel = OperationLogLevelEnum.INFO, opeDesc = "登录")
	public ResponseResult<AdminUserRes> login1421Login(HttpServletRequest request, @RequestBody LoginReq req) {
		try {
			req.setDeviceNumber(request.getHeader("User-Agent"));
			req.checkParam4AdminLogin();
			return loginService.login(req);
		} catch (BizException e) {
			return new ResponseResult(e);
		}
	}

	@ApiOperation(value = "登录", httpMethod = "POST")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	@OpeAnnotation(typeName = OperationTypeEnum.SYSTEM_LOGIN, logLevel = OperationLogLevelEnum.INFO, opeDesc = "登录")
	public ResponseResult login(HttpServletRequest request, @RequestBody LoginReq req) {
		try {
			return new ResponseResult(new BizException("用户名或密码不正确"));
		} catch (BizException e) {
			return new ResponseResult(e);
		}
	}

	@OpeAnnotation(modelName = OperationModuleEnum.SYSTEM_MANAGE, typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改密码")
	@ApiOperation(value = "修改密码", httpMethod = "POST")
	@RequestMapping(value = "/pwd/update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult updateAdminUser(HttpServletRequest request, @RequestBody UpdateAdminUserReq req) {
		req.setUpdateAdminUserId(jwtUtil.getUserId(request));
		req.setRealName(null);
		return adminUserService.updateAdminUser(req);
	}

	@ApiOperation(value = "发送邮箱验证码", httpMethod = "POST")
	@RequestMapping(value = "/sendEmailCode", method = RequestMethod.POST)
	@ResponseBody
	@OpeAnnotation(typeName = OperationTypeEnum.SEND_EMAIL_CODE, logLevel = OperationLogLevelEnum.INFO, opeDesc = "发送邮箱验证码")
	public ResponseResult sendEmailCode(HttpServletRequest request,
			@ApiParam(name = "req", value = "{\"username\":\"账号名称\"}") @RequestBody SendEmailRequest req) {
		try {
			loginService.sendEmailCode(req);
			return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
		} catch (BizException e) {
			log.error("发送邮箱验证码出现业务异常：{}",
					JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.getMsg())));
			return new ResponseResult(ErrorInfoEnum.FAILED.getCode(), e.getMsg());
		} catch (Exception e) {
			log.error("发送验证异常:{}", e.toString(), e);
			return new ResponseResult(new BizException((ErrorInfoEnum.FAILED)));
		}
	}
}
