package com.xinlian.member.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.exception.NoAccessException;
import com.xinlian.common.exception.ReLoginException;
import com.xinlian.common.exception.ReqParamException;
import com.xinlian.common.exception.UpdateVersionException;
import com.xinlian.common.exception.XlApiException;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;

@ControllerAdvice
public class ControllerExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler(ReLoginException.class)
	@ResponseBody
	public ResponseResult reLoginException(ReLoginException e) {
		return e.exceptionMsg();
	}

	@ExceptionHandler(NoAccessException.class)
	@ResponseBody
	public ResponseResult noAccessException(NoAccessException e) {
		return e.exceptionMsg();
	}

	@ExceptionHandler(ReqParamException.class)
	@ResponseBody
	public ResponseResult reqParamException(ReqParamException e) {
		return e.exceptionMsg();
	}

	@ExceptionHandler(BizException.class)
	@ResponseBody
	public ResponseResult BizException(BizException e) {
		return ResponseResult.builder().msg(e.getMsg()).code(0).build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseResult allException(Exception e) {
		logger.error(e.getMessage(), e);
		ResponseResult result = new ResponseResult();
		result.responseResult(GlobalConstant.ResponseCode.SYS_ERROR, "系统繁忙");
		return result;
	}

	@ExceptionHandler(XlApiException.class)
	@ResponseBody
	public ResponseResult otherAreaException(XlApiException e) {
		return ResponseResult.builder().msg(e.getBusiExcpMesg()).code(e.getErrorCode().getCode()).build();
	}

	@ExceptionHandler(UpdateVersionException.class)
	@ResponseBody
	public ResponseResult checkUpdateVersionException(UpdateVersionException e) {
		return ResponseResult.builder().msg(e.getMsg()).code(e.getCode())
				.updateVersion(JSONObject.toJSONString(e.getUpdateVersion())).build();
	}
}
