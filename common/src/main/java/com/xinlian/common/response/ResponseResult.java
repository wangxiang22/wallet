package com.xinlian.common.response;

import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseResult<T> {
	private Integer code;
	private String status;
	private T result;
	private String msg;
	private String updateVersion;

	public static ResponseResult ok() {
		return ResponseResult.builder().code(200).msg("请求成功").build();
	}

	public static ResponseResult ok(Object result) {
		return ResponseResult.builder().code(200).msg("请求成功").result(result).build();
	}

	public static ResponseResult error() {
		return ResponseResult.builder().code(201).msg("请求失败").build();
	}

	public static ResponseResult error(String errorMessage) {
		return ResponseResult.builder().code(201).msg(errorMessage).build();
	}

	public ResponseResult() {
	}

	public ResponseResult(T t) {
		this.code = ErrorInfoEnum.SUCCESS.getCode();
		this.result = t;
	}

	public ResponseResult(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public ResponseResult(BizException bizException) {
		this(bizException.getCode(), bizException.getMsg());
	}

	public ResponseResult(ErrorInfoEnum errorInfoEnum) {
		this(errorInfoEnum.getCode(), errorInfoEnum.getMsg());
	}

	public void responseResult(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public void responseResult(Integer code, T result) {
		this.code = code;
		this.result = result;
	}

	public void responseResult(Integer code, String status, String msg) {
		this.code = code;
		this.status = status;
		this.msg = msg;
	}

	public void responseResult(Integer code, String status, T result) {
		this.code = code;
		this.status = status;
		this.result = result;
	}

	public void responseResult(Integer code, String status, T data, String msg) {
		this.code = code;
		this.status = status;
		this.result = data;
		this.msg = msg;
	}

}
