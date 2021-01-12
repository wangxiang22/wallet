package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.jwt.properties.JwtPropertie;
import com.xinlian.admin.biz.jwt.util.EncryptionUtil;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.AdminMenuService;
import com.xinlian.admin.biz.service.AliyunEmailService;
import com.xinlian.admin.biz.service.LoginService;
import com.xinlian.admin.server.vo.UserLoginSession;
import com.xinlian.biz.dao.AdminRoleInterfaceMapper;
import com.xinlian.biz.dao.AdminRoleMapper;
import com.xinlian.biz.dao.AdminUserMapper;
import com.xinlian.biz.model.AdminMenuModel;
import com.xinlian.biz.model.AdminRoleModel;
import com.xinlian.biz.model.AdminUser;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.request.LoginReq;
import com.xinlian.common.request.SendEmailRequest;
import com.xinlian.common.response.AdminUserRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

	@Autowired
	private AdminUserMapper adminUserMapper;
	@Autowired
	private AdminRoleMapper adminRoleMapper;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private JwtPropertie jwtPropertie;
	@Autowired
	private AdminMenuService adminMenuService;
	@Autowired
	private AliyunEmailService aliyunEmailService;
	@Value("${system.admin.environment}")
	private String adminEnvironmentValue;
	@Autowired
	private AdminRoleInterfaceMapper adminRoleInterfaceMapper;

	@Override
	public ResponseResult<AdminUserRes> login(LoginReq req) {
		ResponseResult<AdminUserRes> result = new ResponseResult<>();
		result.setCode(GlobalConstant.ResponseCode.FAIL);

		// 根据username查询用户信息
		AdminUser user = adminUserMapper.selectOne(new AdminUser(req.getUsername()));
		if (null == user) {
			log.error("此用户不存在,{}", req.getUsername());
			result.setMsg("用户名或密码不正确");
			return result;
		}

		// 验证邮箱验证码
		String redisKey = RedisConstant.REDIS_KEY_LOGIN_EMAIL_CODE + user.getEmailAddress();
		String getSendEmailCode = redisClient.get(redisKey);
		if (!"test20200323".toUpperCase().equals(adminEnvironmentValue.toUpperCase())
				&& (StringUtils.isBlank(getSendEmailCode) || !getSendEmailCode.equals(req.getEmailCoe()))) {
			result.setMsg("输入验证码不正确或者已过期!");
			return result;
		}

		if (null != user.getStatus() && (user.getStatus().intValue() == 0 || user.getStatus().intValue() == 2)) {
			log.error("此用户已被禁用或者删除,{}", req.getUsername());
			result.setMsg("用户名或密码不正确");
			return result;
		}

		String pwd = EncryptionUtil.md5Two(req.getPassword(), user.getSalt());
		if (!pwd.equals(user.getPassword())) {
			result.setMsg("用户名或密码不正确");
			return result;
		}

		// 2.存放值
		// 2.1获取系统登录用户所属角色集合
		List<AdminRoleModel> roles = adminRoleMapper.getRoleByUserId(user.getId());
		if (null == roles || roles.size() == 0) {
			throw new BizException("无获取到该登录用户角色，请联系大山!");
		}
		// 2.2查询登录系统用户对应菜单 查询用户->角色->菜单列表
		List<AdminMenuModel> menus = adminMenuService.getMenuListByRoleModels(roles);
		if (null == menus || menus.size() == 0) {
			throw new BizException("无获取到该登录用户对应菜单，请联系大山!");
		}

		String token = jwtPropertie.getTokenPrefix() + jwtUtil.createToken(user.getId(), req.getDeviceNumber(), System.currentTimeMillis()+"");
		AdminUserRes res = user.adminUserRes();
		res.setToken(token);
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setResult(res);
		// token - redis
		String md5Token = DigestUtils.md5Hex(token.substring("Bearer ".length()));
		// 存放登录相关信息到redis中
		String ticketMenu = DigestUtils.md5Hex(md5Token + RedisConstant.REDIS_KEY_SESSION_MENU),
//				ticketLabel = DigestUtils.md5Hex(md5Token + RedisConstant.REDIS_KEY_SESSION_LABEL),
				ticketRole = DigestUtils.md5Hex(md5Token + RedisConstant.REDIS_KEY_SESSION_ROLE),
				ticketReqInterfaceUrl = DigestUtils.md5Hex(md5Token + RedisConstant.REDIS_KEY_SESSION_REQ_INTERFACE_URL);
		long pastDueTime = 60 * 60L;
		//限制一个账号只能登录一个设备 start
		String ssoLoginUidKey = "SSO_WALLET_ADMIN_".concat(res.getUserId().toString());
		String ssoMd5Token = DigestUtils.md5Hex(token.substring("Bearer ".length()).concat(req.getDeviceNumber()));
		redisClient.set(ssoLoginUidKey, ssoMd5Token, pastDueTime);
		//限制一个账号只能登录一个设备 end
		String loginKey = RedisConstant.REDIS_KEY_SESSION_USERID + res.getUserId();
		//角色对应接口请求地址
		List<String> interfaceUrl = adminRoleInterfaceMapper.getInterfaceReqUrlByRoleIds(roles);
		//存缓存
		redisClient.set(loginKey, md5Token,pastDueTime);
		redisClient.set(ticketRole, roles, pastDueTime);
		redisClient.set(ticketMenu, menus, pastDueTime);
		redisClient.set(ticketReqInterfaceUrl,interfaceUrl,pastDueTime);
		UserLoginSession userLoginSession = new UserLoginSession(res, md5Token, ticketMenu, ticketRole, ticketReqInterfaceUrl);
		redisClient.set(md5Token, userLoginSession, pastDueTime);
		return result;
	}

	@Override
	public void sendEmailCode(SendEmailRequest req) {
		// 获取req.username查找对应的email名称
		String toEmailAddress = adminUserMapper.getEmailByUserName(req.getUsername());
		if (null == toEmailAddress) {
			throw new BizException("账号异常!");
		}
		if (!toEmailAddress
				.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
			throw new BizException("邮箱格式有误!");
		}
		String emailCode = EncryptionUtil.getRandomCode(6);
		boolean sendFlag = aliyunEmailService.sendEmailCode(toEmailAddress, emailCode,
				MailTemplateEnum.ADMIN_SYSTEM_LOGIN_EMAIL_CODE.getCode());
		if (!sendFlag) {
			throw new BizException("发送验证码过于频繁,请稍后重试!");
		}
		String redisKey = RedisConstant.REDIS_KEY_LOGIN_EMAIL_CODE + toEmailAddress;
		redisClient.set(redisKey, emailCode, 3 * 60l);
	}

	// 请求url路径还得单独表来配置
	private void packageUrlRoles(List<AdminMenuModel> menus, List<AdminRoleModel> roles) {
		for (AdminMenuModel menuModel : menus) {

		}
	}

}
