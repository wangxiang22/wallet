package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.enums.SystemChannelEnum;
import com.xinlian.common.request.MailSendReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IServerNodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * com.xinlian.member.server.controller.handler
 *
 * @author by Song
 * @date 2020/6/19 19:35
 */
@Slf4j
@Component
public class RegisterHandler {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Value("${swaggerAuth}")
    private String swaggerAuth;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;

	/**
	 * 人机参数检验
	 * @param adPercent
	 * @return
	 */
	public void checkAdPercent(String adPercent){
		String requestHeader = httpServletRequest.getHeader("DeviceNumber");
		if (StringUtils.isBlank(requestHeader)) {
			throw new BizException("参数检验错误,请再尝试下!");
		}
		String redisValue = redisClient.get(requestHeader);
		if (StringUtils.isBlank(redisValue) || !redisValue.equals(adPercent)) {
			throw new BizException("人机参数检验异常!");
		}
	}

	/**
	 * 注册之前需要做的国外节点动作判断
	 * 只为大航海节点及子节点用
	 * @param req
	 */
	public void judgeAbroadNodeIsRegister(RegisterReq req) {
		this.judgeAbroadNodeIsRegister(req.getNodeId(),req.getPhone());
	}

    public void judgeAbroadNodeIsRegister(@NotNull Long nodeId, @NotNull String phone) {
		// 查询节点
		TServerNode serverNode = serverNodeService.getById(nodeId);
		if (null == serverNode) {
			throw new BizException("不存在此节点信息!");
		}
		Integer mobileRegisterNum = serverNode.getMobileRegisterAmount();
		// 1.获取当前节点手机号注册量
		if (-1 != mobileRegisterNum) {
			int getMobileRegisterNumber = userInfoMapper.getNodePhoneStayActivateNumber(nodeId, phone);
			if (getMobileRegisterNumber >= mobileRegisterNum) {
				throw new BizException("注册账号数量已达上限，请更换手机号或节点");
			}
		}
	}

	/**
	 * 注册之前需要做的国外节点动作判断
	 *
	 * @param nodeId 节点
	 * @param email 邮箱
	 */
	public void judgeAbroadNodeIsEmailRegister(Long nodeId,String email) {
		// 查询节点
		TServerNode serverNode = serverNodeService.getById(nodeId);
		if (null == serverNode) {
			throw new BizException("不存在此节点信息!");
		}
		Integer limitEmailRegisterNum = serverNode.getEmailBindAmount();
		// 1.获取当前节点手机号注册量
		if (-1 != limitEmailRegisterNum) {
			int getEmailRegisterNumber = userInfoMapper.getNodeEmailStayActivateNumber(nodeId,
					email);
			if (getEmailRegisterNumber >= limitEmailRegisterNum) {
				throw new BizException("注册账号数量已达上限，请更换邮箱或节点");
			}
		}
	}

	public RegisterReq decodeDataToObject(Map<String, String> paramMap) {
		return decodeDataToObject(paramMap, RegisterReq.class);
	}

	public <T> T decodeDataToObject(Map<String, String> paramMap, Class<T> clazz) {
		T entity ;
 		try {
			String systemChannel = paramMap.get("systemChannel");
			String stayDecodeData = paramMap.get("data");
			String decodeData = "";
			if (null != systemChannel && SystemChannelEnum.APP_H5.getChannelCode().equals(systemChannel)) {
				String privateKey = adminOptionsUtil.findAdminOptionOne(
						AdminOptionsBelongsSystemCodeEnum.H5_RSA_PRIVATE_KEY.getBelongsSystemCode());
				byte[] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(stayDecodeData), privateKey);
				decodeData = new String(bvy);
			} else {
				String privateKey = "";
				if (null != swaggerAuth && "gray".equals(swaggerAuth)) {
					log.info("走gray");
					privateKey = adminOptionsUtil.findAdminOptionOne(
							AdminOptionsBelongsSystemCodeEnum.APP_RSA_GRAY_PRIVATE_KEY.getBelongsSystemCode());
				} else {
					log.info("走online");
					privateKey = adminOptionsUtil.findAdminOptionOne(
							AdminOptionsBelongsSystemCodeEnum.APP_RSA_PRIVATE_KEY.getBelongsSystemCode());
				}
				byte[] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(stayDecodeData), privateKey);
				decodeData = new String(bvy);
			}
			entity = JSONObject.parseObject(decodeData, clazz);
		} catch (Exception e) {
			log.error("解密出现异常!,{}", e.toString(), e);
			throw new BizException("网络请求异常，请稍后重试!");
		}
		return entity;
	}

	public void checkWhetherNeedSendEmail(HttpServletRequest httpServletRequest, String disableEmailTips,
			MailSendReq mailSendReq) {
		try {
			Long userId, nodeId;
			TUserInfo checkUserInfo = null;
			if (MailTemplateEnum.FORGET_THE_LOGIN_PASSWORD.getCode().intValue() == mailSendReq.getUseType()
					.intValue()) {
				log.info("更改登录密码-send mail..-:{}", JSONObject.toJSONString(mailSendReq));
				// 忘记登录密码
				checkUserInfo = redisClient.get(mailSendReq.getSign());
				userId = null == checkUserInfo ? null : checkUserInfo.getUid();
				nodeId = null == checkUserInfo ? null : checkUserInfo.getServerNodeId();
			} else {
				userId = jwtUtil.getUserId(httpServletRequest);
				nodeId = jwtUtil.getNodeId(httpServletRequest);
			}
			if (null == userId || null == nodeId) {
				throw new BizException(disableEmailTips);
			}
			if (!(nodeId.intValue() == 107 || nodeId.intValue() == 108)) {
				throw new BizException(disableEmailTips);
			}
			if (null == checkUserInfo) {
				checkUserInfo = userInfoMapper.selectById(userId);
			}
			if (null == checkUserInfo || (null != checkUserInfo.getMobile()
					&& StringUtils.isNotEmpty(checkUserInfo.getMobile().trim()))) {
				throw new BizException(disableEmailTips);
			}
			if (StringUtils.isEmpty(checkUserInfo.getEmail())) {
				throw new BizException(disableEmailTips);
			}
		} catch (BizException e) {
			throw new BizException(e.getMsg());
		} catch (Exception e) {
			log.error("registerHandler checkWhetherNeedSendEmail is error:{}", e.toString(), e);
			throw new BizException(disableEmailTips);
		}
	}

    public void checkRegisterFromIpDoMuch(){
        int defaultLimitNum = 20;
        Long oneDaySecond = 24*60*60L;
        String getLimitNum = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.REQUEST_URI_LIMIT_NUMBER.getBelongsSystemCode());
        String requestUriPeriod = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.REQUEST_URI_LIMIT_PERIOD.getBelongsSystemCode());
        if(StringUtils.isNoneBlank(getLimitNum)){
            defaultLimitNum = Integer.parseInt(getLimitNum);
        }
        if(StringUtils.isNoneBlank(requestUriPeriod)){
            oneDaySecond = Long.parseLong(requestUriPeriod);
        }
        //校验ip次数
        String ipAddr = SystemUtils.getIpAddress(httpServletRequest);
        log.info("当前注册用户ip为：{}", ipAddr);
        String redisKey = RedisConstant.APP_REDIS_PREFIX.concat("register_count_").concat(ipAddr);
        Long count = luaScriptRedisService.doIncr(redisKey,oneDaySecond);
        if(count > defaultLimitNum){
            throw new BizException("注册出现异常,请稍后重试");
        }
    }

}
