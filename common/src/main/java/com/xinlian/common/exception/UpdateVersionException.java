package com.xinlian.common.exception;

import com.xinlian.biz.model.TUpdateVersion;

public class UpdateVersionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Integer code;
	private TUpdateVersion updateVersion;
	private String msg;

	public UpdateVersionException() {
		super();
	}

	public UpdateVersionException(Integer code, TUpdateVersion updateVersion, String msg) {
		super();
		this.code = code;
		this.updateVersion = updateVersion;
		this.msg = msg;
	}

	public TUpdateVersion getUpdateVersion() {
		return updateVersion;
	}

	public void setUpdateVersion(TUpdateVersion updateVersion) {
		this.updateVersion = updateVersion;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
