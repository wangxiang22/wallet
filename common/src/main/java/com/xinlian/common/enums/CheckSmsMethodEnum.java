package com.xinlian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * sms - method
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckSmsMethodEnum {

	SMS_QUERY_ACCOUNT_NUMBER("SMS_QUERY_ACCOUNT_NUMBER", "查询工具账号"),

	SELL_CAT("SELL_CAT", "智能合约卖出cat"),

	BUY_CAT("BUY_CAT", "智能合约买入cat"),

	BLOCKMALL_CERT("BLOCKMALL_CERT", "布鲁克商城哥伦布会员认证"),
	
	BLOCKMALL_PAY("BLOCKMALL_PAY", "布鲁克商城哥伦布钱包支付"),

	;

	String methodCode;

	String methodDesc;

}
