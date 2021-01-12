package com.xinlian.common.request;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class LoginReq implements ICheckParam {
	private Long nodeId;
	private String username;
	private String password;
	// 邮箱验证码
	private String emailCoe;
	private String deviceNumber;

	private String loginIp;
	// 经度
	private String longitude;
	// 纬度
	private String latitude;
	// 极光id
	private String jid;
	// 设备类型1安卓2ios
	private Integer type;
	// 省份名称
	private String provinceName;
	// 城市名称
	private String cityName;

	// 待检验数值
	private String adPercent;

	@Override
	public void checkParam() {
		if (nodeId == null) {
			throwException();
		}
		if (StringUtils.isEmpty(username)) {
			throwException();
		}
		if (StringUtils.isEmpty(deviceNumber)) {
			throwException();
		}
	}

	public void checkParam4AdminLogin() {
		if (StringUtils.isBlank(username)) {
			throwException();
		}
		if (StringUtils.isBlank(emailCoe)) {
			throwException();
		}
		if (StringUtils.isBlank(password)) {
			throwException();
		}
		if (StringUtils.isBlank(deviceNumber)) {
			throwException();
		}
	}

	public void checkParam4MemberLogin() {
		if (StringUtils.isBlank(adPercent)) {
			throwException();
		}
		if (null == type) {
			throwException();
		}
		if (StringUtils.isBlank(deviceNumber)) {
			throwException();
		}
		if (StringUtils.isBlank(password)) {
			throwException();
		}
		if (StringUtils.isBlank(username)) {
			throwException();
		}
		if (null == nodeId) {
			throwException();
		}
	}
}
