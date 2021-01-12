package com.xinlian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * sms register - method 走发送注册短信接口 type 魔法值集中营
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SendRegisterTypeEnum {

	REGISTER(1, "注册"),

	FORGET_PAY_PWD(2, "忘记支付密码"),

	FORGET_LOGIN_PWD(3, "忘记登录密码"),

	WITHDRAW_CURRENCY(4, "提币"),

	BIND_PHONE(5, "绑定手机号"),

	BLOCKMALL_CERT(810, "布鲁克商城哥伦布会员认证"),
	BLOCKMALL_PAY(811, "布鲁克商城哥伦布钱包支付"),

	;

	int type;

	String desc;

	/**
	 * 绑定手机号 type : 5 忘记登陆密码 type : 3 忘记支付密码 type : 2 注册 type : 1 提币 type : 4
	 */

}
