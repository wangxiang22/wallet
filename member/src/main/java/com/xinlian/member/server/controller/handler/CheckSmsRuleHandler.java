package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Song
 * @date 2020-07-08 20:06
 * @description check sms rule handler
 */
@Slf4j
@Component
public class CheckSmsRuleHandler {

	@Autowired
	private LuaScriptRedisService luaScriptRedisService;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private AdminOptionsUtil adminOptionsUtil;

	/**
	 * 保存sms请求规则方法
	 * 
	 * @param phone  不一定是phone,登录情况下是uid
	 * @param method 所请求方法
	 */
	public void doSaveSmsRuleHandler(String phone, String method) {
		this.doSaveApplicationRequest(this.getRequestKey(phone, method));
	}

	/**
	 * 检验sms是否存在不可多次请求规则方法
	 * 
	 * @param phone  不一定是phone,登录情况下是uid
	 * @param method 所请求方法
	 */
	public void doCheckSmsRuleHandler(String phone, String method) {
		this.doCheckApplicationRequest(this.getRequestKey(phone, method));
	}

	public void doDeleteSmsRuleHandler(String phone, String method) {
		this.doRemoveSmsRuleHandler(this.getRequestKey(phone, method));
	}

	private String getRequestKey(String phone, String method) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(phone);
		stringBuffer.append("-");
		stringBuffer.append(method);
		return stringBuffer.toString();
	}

	private void doSaveApplicationRequest(String requestKey) {
		String hourPhoneRedisKey = this.getCheckHourRuleKey(requestKey);
		String dayPhoneRedisKey = this.getCheckDayRuleKey(requestKey);
		luaScriptRedisService.doIncr(hourPhoneRedisKey, 60 * 60L);
		luaScriptRedisService.doIncr(dayPhoneRedisKey, 24 * 60 * 60L);
	}

	private String getCheckHourRuleKey(String redisKeySuffix) {
		return RedisConstant.APP_REDIS_PREFIX + "HOUR_" + redisKeySuffix;
	}

	private String getCheckDayRuleKey(String redisKeySuffix) {
		return RedisConstant.APP_REDIS_PREFIX + "DAY_" + redisKeySuffix;
	}

	private void doCheckApplicationRequest(String stayCheckRequestKey) {
		String hourPhoneRedisKey = this.getCheckHourRuleKey(stayCheckRequestKey);
		String dayPhoneRedisKey = this.getCheckDayRuleKey(stayCheckRequestKey);
		Integer hourNum = redisClient.get(hourPhoneRedisKey);
		Integer dayNum = redisClient.get(dayPhoneRedisKey);

		String smsHourErrorNumStr = adminOptionsUtil
				.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SMS_HOUR_ERROR_NUM.getBelongsSystemCode());// 7
		String smsDayErrorNumStr = adminOptionsUtil
				.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SMS_DAY_ERROR_NUM.getBelongsSystemCode());// 14

		int checkHourErrorNum = 5;
		int checkDayErrorNum = 10;

		if (null != smsHourErrorNumStr) {
			checkHourErrorNum = Integer.valueOf(smsHourErrorNumStr);// Long.parseLong(smsHourErrorNumStr);
		}
		if (null != smsDayErrorNumStr) {
			checkDayErrorNum = Integer.valueOf(smsDayErrorNumStr);
		}

		if (null != hourNum && hourNum.intValue() >= checkHourErrorNum && hourNum.intValue() < checkDayErrorNum) {
			log.error(DateFormatUtil.get(7, new Date()) + "check sms rule key:" + hourPhoneRedisKey);
			throw new BizException("错误超过" + hourNum + "次,请一小时后尝试!");
		}

		if (null != dayNum && dayNum.intValue() >= checkDayErrorNum) {
			log.error(DateFormatUtil.get(7, new Date()) + "check sms rule key:" + dayPhoneRedisKey);
			throw new BizException("错误超过" + dayNum + "次,请二十四小时后尝试!");
		}
	}

	public void doRemoveSmsRuleHandler(String stayCheckRequestKey) {
		String hourPhoneRedisKey = this.getCheckHourRuleKey(stayCheckRequestKey);
		String dayPhoneRedisKey = this.getCheckDayRuleKey(stayCheckRequestKey);
		redisClient.deleteByKey(hourPhoneRedisKey);
		redisClient.deleteByKey(dayPhoneRedisKey);
	}

}
