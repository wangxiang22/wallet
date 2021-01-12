package com.xinlian.member.biz.alisms.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsUtil {
	protected final static Logger logger = LoggerFactory.getLogger(SmsUtil.class);

	// 产品名称:云通信短信API产品,开发者无需替换
	static final String product = "Dysmsapi";
	// 产品域名,开发者无需替换
	static final String domain = "dysmsapi.aliyuncs.com";

	public static String createPhoneKey(String phone, int reqType) {
		return "SMS_CODE_" + reqType + "_" + phone;
	}

	public static String createPhoneKey(String phone) {
		return "SMS_CODE_" + phone;
	}

	public static void main(String[] args) throws Exception {

	}

	public static String getCountryCodeAndPhone(String phone, int countryCode) {
		if (StringUtils.isBlank(phone)) {
			return "";
		}
		return 86 == countryCode ? phone : countryCode + phone;
	}

}
