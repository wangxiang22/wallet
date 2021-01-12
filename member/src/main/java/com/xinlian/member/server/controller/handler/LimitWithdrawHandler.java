package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author Song
 * @date 2020-06-20 11:04
 * @description 限制交易handler
 */
@Component
@Slf4j
public class LimitWithdrawHandler {

	@Autowired
	private AdminOptionsUtil adminOptionsUtil;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private TUserInfoMapper userInfoMapper;

	/**
	 * 根据币种id,uid 判断是否在限制名单中
	 *
	 * @param coinId
	 * @param uid
	 */
	public void doLimitWithdraw(String coinId, Long uid) {
		String limitWithdrawUidsValue = adminOptionsUtil
				.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.LIMIT_WITHDRAW_UIDS.getBelongsSystemCode());
		String limitWithdrawCurrencyValue = adminOptionsUtil
				.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.LIMIT_WITHDRAW_CURRENCY.getBelongsSystemCode());
		if (null != limitWithdrawUidsValue && null != limitWithdrawCurrencyValue
				&& limitWithdrawCurrencyValue.contains(coinId)) {
			String[] uidArray = limitWithdrawUidsValue.split(",");
			List limitUidsList = Arrays.asList(uidArray);
			// log.error("提币验证是否在uids集合里面:{},uid:{}",
			// JSONObject.toJSONString(limitUidsList),uid);
			if (limitUidsList.contains(uid.toString())) {
				throw new BizException("暂未开放!");
			}
		}
	}

	/**
	 * 根据serverNodeId 判断是否在限制注册节点id
	 *
	 * @param req
	 */
	public void doLimitServerNodeId(RegisterReq req) {
		if(req.getType() != 1 || null == req.getNodeId()){
			return;
		}
		String serverNodeIdStr = req.getNodeId().toString();
		String limitServerNodeIds = adminOptionsUtil.findAdminOptionOne(
				AdminOptionsBelongsSystemCodeEnum.LIMIT_REGISTER_SERVER_NODE_ID.getBelongsSystemCode());
		if (StringUtils.isNotBlank(limitServerNodeIds)) {
			String[] serverNodeIdArray = limitServerNodeIds.split(",");
			List<String> limitServerNodeIdList = Arrays.asList(serverNodeIdArray);
			if (null != limitServerNodeIdList && limitServerNodeIdList.contains(serverNodeIdStr)) {
				throw new BizException("该节点暂未开放注册!");
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param req
	 */
	public int getUserInfoCountryCodeByReqtype(RegisterReq req) {
		String forgetPasswordType = redisClient.get("forgetPasswordTypes");
		if (null != forgetPasswordType) {
			String[] typeArray = forgetPasswordType.split(",");
			List typeList = Arrays.asList(typeArray);
			if (typeList.contains(req.getType())) {
				TUserInfo whereUserInfo = new TUserInfo();
				whereUserInfo.setUserName(req.getUsername());
				whereUserInfo.setServerNodeId(req.getNodeId());
				TUserInfo getUserInfo = userInfoMapper.getOneModel(whereUserInfo);
				if (null != getUserInfo) {
					return getUserInfo.getCountryCode();
				}
			}
			return req.getCountryCode();
		} else {
			return req.getCountryCode();
		}
	}

}
