package com.xinlian.admin.biz.myshiro.interceptor;

import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.myshiro.filter.JwtFilterMap;
import com.xinlian.admin.biz.redis.AdminLuaScriptRedisService;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.server.vo.UserLoginSession;
import com.xinlian.biz.dao.RequestRecordLogMapper;
import com.xinlian.biz.model.RequestRecordLogModel;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.SystemUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class AuthenticationInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtFilterMap jwtFilterMap;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RequestRecordLogMapper requestRecordLogMapper;
    @Autowired
    private AdminLuaScriptRedisService adminLuaScriptRedisService;

    private static final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<Long>();

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object object) throws Exception {
        long beginTime = System.currentTimeMillis();// 1、开始时间
        startTimeThreadLocal.set(beginTime); // 线程绑定变量（该数据只有当前请求的线程可见）
        String url = httpServletRequest.getServletPath();
        logger.info("访问url：=======================" + url);
        String origin = httpServletRequest.getHeader("Origin");
        //logger.info("跨域拦截执行 {}", origin);
        if (origin != null && !"".equals(origin)) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            httpServletResponse.addHeader("allowCredentials", "true");
            httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Authorization, RefreAuthorization");
            if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH");
                httpServletResponse.addHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, RefreAuthorization");
                httpServletResponse.setStatus(HttpStatus.OK.value());
            }
        }
        List<String> roles = jwtFilterMap.matchRole(url);
        if (null!=roles && roles.contains("none")) {
            return true;
        }
        String token = jwtUtil.getToken(httpServletRequest);
        if(token == null && roles == null){
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
        //token里解析出来的 设备号
        String userAgentToken = jwtUtil.getUserAgent(token);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (!jwtUtil.verifyToken(token) || !userAgentToken.equals(userAgent)) {
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
        Long userId = jwtUtil.getUserId(token);
        if(null == redisClient.get(RedisConstant.REDIS_KEY_SESSION_USERID + userId)){
            logger.error("用户已被修改密码或删除 uid=={}", userId);
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
        //限制账号多登
        this.checkAccountMuch(token,userAgent);
        //用户有没有 被禁用
        if(redisClient.get(RedisKeys.freezeAdminUserKey(jwtUtil.getUserId(token))) != null){
            logger.info("用户已被禁用 userId=={}", userId);
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
        //check request url is call on
        this.checkRequestIpNum(httpServletRequest,url,userId);
        //the interface has permissions
        this.checkInterfaceHasPermissions(httpServletRequest,url);
        return true;
    }

    private void checkInterfaceHasPermissions(HttpServletRequest httpServletRequest,String reqUrl){
        UserLoginSession userLoginSession = jwtUtil.getUserLoginSession(httpServletRequest);
        String ticketInterfaceUrl = userLoginSession.getTicketInterfaceUrl();
        logger.info(DateFormatUtil.getByNowTime(7) + " login req interface get together - redis key "+ ticketInterfaceUrl);
        List<String> interfaceUrls = redisClient.get(ticketInterfaceUrl);
        if(!CollectionUtils.isEmpty(interfaceUrls) && !interfaceUrls.contains(reqUrl)){
            logger.error(DateFormatUtil.getByNowTime(7) + " 未获取到该请求权限 reqUrl:"+reqUrl + "userId:"+jwtUtil.getUserId(httpServletRequest));
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
    }

    private void checkRequestIpNum(HttpServletRequest httpServletRequest,String requestUrl,Long userId) {
        String reqIp = this.getRequestIp(httpServletRequest);
        if (StringUtils.isEmpty(reqIp)) {
            return;
        }
        String nonRestrictionIp = redisClient.get(RedisConstant.ADMIN_REDIS_PREFIX + "RESTRICT_IPS");
        if (null != nonRestrictionIp && nonRestrictionIp.indexOf(reqIp) > -1) {
            return;
        }
        String lockIpRedisKey = RedisConstant.ADMIN_REDIS_PREFIX.concat("LOCK_IP");

        Long reqIpRank = redisClient.zrank(lockIpRedisKey,reqIp);
        if(null!=reqIpRank && reqIpRank.intValue()>=0){
            logger.error(DateFormatUtil.getByNowTime(7) + " wallet admin check ip in limit ips : " + reqIp +
                    ";requestUrl : " + requestUrl + ";请求次数 : " + redisClient.zsetCurrentScore(lockIpRedisKey,reqIp));
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }

        String redisKey = RedisConstant.ADMIN_REDIS_PREFIX.concat("CHECK_IP_REQUEST_URL").concat(requestUrl).concat(userId.toString());
        Long ipRequestNum = adminLuaScriptRedisService.doIncr(redisKey, 1L);
        if (ipRequestNum.intValue() >= 10) {
            redisClient.zincrementScore(lockIpRedisKey,reqIp,1,24*60*60L);
            logger.error(DateFormatUtil.getByNowTime(7) + " wallet admin add ip request in limit 5 : " + reqIp +
                    ";requestUrl : " + requestUrl);
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
    }

    private void checkAccountMuch(String token,String userAgent){
        String ssoMd5Token = DigestUtils.md5Hex(token.concat(userAgent));
        Long userId = jwtUtil.getUserId(token);
        String ssoLoginUidKey = "SSO_WALLET_ADMIN_".concat(userId.toString());
        String getMd5TokenByRedis = redisClient.get(ssoLoginUidKey);
        if(null == getMd5TokenByRedis || !getMd5TokenByRedis.equals(ssoMd5Token)){
            logger.info("用户已被其他地方登录 userId=={}", userId);
            throw new BizException(ErrorInfoEnum.AFRESH_LOGIN_CODE);
        }
    }


    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
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
            RequestRecordLogModel model = new RequestRecordLogModel();
            model.setTaskTime(taskTime);
            model.setRequestUrl(requestUrl);
            model.setJvmParam(jvmParam);
            model.setUid(uid);
            model.setRequestIp(requestIp);
            model.setServerIntranetIp(SystemUtils.getLocalAddress());
            model.setBelongSystem("ADMIN");
            new Thread(() -> {
                requestRecordLogMapper.threadSaveRequestRecordLog(model);
            }).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
