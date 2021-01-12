package com.xinlian.member.biz.jwt.interceptor;

import com.xinlian.biz.dao.RequestRecordLogMapper;
import com.xinlian.biz.model.RequestRecordLogModel;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.exception.ReLoginException;
import com.xinlian.common.exception.UpdateVersionException;
import com.xinlian.common.exception.XlApiException;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TUpdateVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private LuaScriptRedisService luaScriptRedisService;
	@Autowired
	private TUpdateVersionService updateVersionService;
	@Autowired
	private RequestRecordLogMapper requestRecordLogMapper;
	@Value("${swaggerAuth}")
	private String swaggerAuth;

	private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<Long>();

	private void checkRequestIpNum(HttpServletRequest httpServletRequest) {
		String reqIp = SystemUtils.getIpAddress(httpServletRequest);
		if (StringUtils.isEmpty(reqIp)) {
			return;
		}
		String nonRestrictionIp = redisClient.get(RedisConstant.APP_REDIS_PREFIX + "RESTRICT_IPS");
		if (null != nonRestrictionIp && nonRestrictionIp.indexOf(reqIp) > -1) {
			return;
		}
		String redisKey = RedisConstant.APP_REDIS_PREFIX + "CHECK_IP_" + reqIp;
		Long ipRequestNum = luaScriptRedisService.doIncr(redisKey, 1L);
		if (ipRequestNum.intValue() > 500) {
			throw new BizException("前方拥堵，请稍后再次访问!");
		}
	}

	private void checkRequestUrlNum(String requestUrl) {
		String nonRequestUrls = redisClient.get(RedisConstant.APP_REDIS_PREFIX + "REQUEST_URL");
		if (null != nonRequestUrls && nonRequestUrls.indexOf(requestUrl) > -1) {
			return;
		}
		String redisKey = RedisConstant.APP_REDIS_PREFIX + "CHECK_URL_" + requestUrl;
		Long urlRequestNum = luaScriptRedisService.doIncr(redisKey, 1L);
		if (urlRequestNum.intValue() > 1000) {
			throw new BizException("前方拥堵，请稍后再次访问!");
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object object) throws Exception {
		long beginTime = System.currentTimeMillis();// 1、开始时间
		startTimeThreadLocal.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
		log.info("开始计时: {}  URI: {}", DateFormatUtil.get(1, new Date()), httpServletRequest.getRequestURI());
		String url = httpServletRequest.getServletPath();
		log.info("url================={}", url);
		if (url.startsWith("/cloud")) {
			return true;
		}
		// 验证请求url
		this.checkRequestUrlNum(url);
		// 验证请求ip
		this.checkRequestIpNum(httpServletRequest);
		if ("a3u0t40h2020".equals(swaggerAuth) && (url.startsWith("/swagger-resources") || url.startsWith("/webjars")
				|| url.startsWith("/v2") || url.startsWith("/swagger-ui.html"))) {
			return true;
		}
		// 如果不是映射到方法直接通过
		if (!(object instanceof HandlerMethod)) {
			return true;
		}

		this.checkUpdateVersion(httpServletRequest);

		Method method = ((HandlerMethod) object).getMethod();
		// 检查是否有passtoken注释，有则跳过认证
		if (method.isAnnotationPresent(PassToken.class)) {
			return true;
		}
		Map pathVariables = (Map) httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String versionPath = (String) pathVariables.get("versionPath");
		if (null != versionPath && !"v3".equals(versionPath)) {
			throw new BizException("参数异常，请确认参数!");
		} else if ("/error".equals(url)) {
			throw new BizException("请升级到最新版本!");
		}

		// token验证
		String token = httpServletRequest.getHeader("Authorization");
		String userAgent = httpServletRequest.getHeader("DeviceNumber");
		if (token == null || userAgent == null) {
			log.info("token==={} DeviceNumber==={}", token, userAgent);
			throw new ReLoginException();
		}
		try {
			token = token.substring("Bearer ".length());
		} catch (Exception e) {
			log.info("token异常======{}", token);
			throw new ReLoginException();
		}
		// 执行认证
		if (!jwtUtil.verifyToken(token)) {
			log.info("token失效======{}", token);
			throw new ReLoginException();
		}
		// 不是同一个设备号 可能token被盗用
		if (!userAgent.equals(jwtUtil.getUserAgent(token))) {
			log.info("用户DeviceNumber不一致");
			throw new ReLoginException();
		}
		// 用户有没有 被禁用
		if (redisClient.get(RedisKeys.freezeUserKey(jwtUtil.getUserId(token))) != null) {
			log.info("用户已被禁用 uid=={}", jwtUtil.getUserId(token));
			throw new ReLoginException();
		}
		// 单点登录可加开关
		if (!StringUtils.equals(redisClient.get("sso_" + jwtUtil.getUserId(token)), "Bearer " + token)) {
			throw new XlApiException("用户异地登录，请重新登录", ErrorCode.OTHER_AREA);
		}
		return true;
	}

	private void checkUpdateVersion(HttpServletRequest httpServletRequest) {
		Integer type = null;// 类别，安卓还是苹果，1是安卓，2是苹果
		String currentVersion = httpServletRequest.getHeader("currentVersion_ios");
		if (StringUtils.isNotEmpty(currentVersion)) {
			type = 2;
		} else {
			currentVersion = httpServletRequest.getHeader("currentVersion_apk");
			if (StringUtils.isNotEmpty(currentVersion)) {
				type = 1;
			}
		}
		if (null == type) {
			return;
		}
		TUpdateVersion tUpdateVersion = updateVersionService.queryVersion(type);
		if (null == tUpdateVersion || StringUtils.isBlank(tUpdateVersion.getVersion())
				|| currentVersion.equals(tUpdateVersion.getVersion())) {
			return;
		}
		//版本号比较 和强更
		if(this.checkRequestVersionCompare(currentVersion,tUpdateVersion.getVersion())){
			if (null != tUpdateVersion.getForceUpdate() && 1 == tUpdateVersion.getForceUpdate()) {
				throw new UpdateVersionException(ErrorInfoEnum.NEW_VERSION.getCode(), tUpdateVersion,
						ErrorInfoEnum.NEW_VERSION.getMsg());
			} else {
				String userAgent = httpServletRequest.getHeader("DeviceNumber");
				if (StringUtils.isNotBlank(userAgent)) {
					String key = RedisConstant.APP_VERSION_PUSHED + userAgent;
					String str = redisClient.get(key);
					if (StringUtils.isBlank(str)) {
						redisClient.setDayResidueTimes(key, "true");
						throw new UpdateVersionException(ErrorInfoEnum.NEW_VERSION.getCode(), tUpdateVersion,
								ErrorInfoEnum.NEW_VERSION.getMsg());
					}
				}
			}
		}
		return;
	}

	private boolean checkRequestVersionCompare(String currentVersion, String dbVersion) {
		try {
			Integer currentVersionInt = Integer.parseInt(currentVersion.replace(".", ""));
			Integer dbVersionInt = Integer.parseInt(dbVersion.replace(".", ""));
			return dbVersionInt.intValue() > currentVersionInt;
		}catch (Exception e){
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception {
		long beginTime = startTimeThreadLocal.get();// 得到线程绑定的局部变量（开始时间）
		long endTime = System.currentTimeMillis(); // 2、结束时间
		String url = httpServletRequest.getServletPath();
		if(url.startsWith("/swagger-resources") || url.startsWith("/webjars")
				|| url.startsWith("/v2") || url.startsWith("/swagger-ui.html")){
			return;
		}
		StringBuffer jvmParam = new StringBuffer();
		jvmParam.append("【最大内存: "+Runtime.getRuntime().maxMemory() / 1024 / 1024+"m】-");
		jvmParam.append("【已分配内存: "+Runtime.getRuntime().totalMemory() / 1024 / 1024+"m】-");
		jvmParam.append("【已分配剩余空间: "+Runtime.getRuntime().freeMemory() / 1024 / 1024+"m】-");
		jvmParam.append("【最大可用内存: " + (Runtime.getRuntime().maxMemory()
				- Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "m】");
		this.packageRecordLogParam(endTime-beginTime,url,jvmParam.toString(),
				this.getUidByRequest(httpServletRequest),this.getRequestIp(httpServletRequest));
	}

    private Long getUidByRequest(HttpServletRequest httpServletRequest){
        try{
            return jwtUtil.getUserId(httpServletRequest);
        }catch (Exception e){
            return -1L;
        }
    }

	private String getRequestIp(HttpServletRequest httpServletRequest){
		return SystemUtils.getIpAddress(httpServletRequest);
	}

    private void packageRecordLogParam(long taskTime, String requestUrl, String jvmParam,Long uid,String requestIp) {
        try {
        	if(taskTime<150){
        		return;
			}
            RequestRecordLogModel model = new RequestRecordLogModel();
            model.setTaskTime(taskTime);
            model.setRequestUrl(requestUrl);
            model.setJvmParam(jvmParam);
            model.setUid(uid);
            model.setRequestIp(requestIp);
            model.setServerIntranetIp(SystemUtils.getLocalAddress());
			model.setBelongSystem("APP");
            new Thread(() -> {
                requestRecordLogMapper.threadSaveRequestRecordLog(model);
            }).start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }



}
