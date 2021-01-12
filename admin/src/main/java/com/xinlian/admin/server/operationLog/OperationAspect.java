package com.xinlian.admin.server.operationLog;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.redis.AdminLuaScriptRedisService;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.OperationLogService;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.response.AdminUserRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.threadpool.ThreadPoolProxySingle;
import com.xinlian.common.utils.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现用户crud操作的日志记录
 *
 * @author liujt
 */
@Aspect
@Component
@Slf4j
public class OperationAspect {

    @Resource
    private OperationLogService operationLogService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminLuaScriptRedisService adminLuaScriptRedisService;
    @Autowired
    private RedisClient redisClient;
    @Value("${system.admin.environment}")
    private String adminEnvironmentValue;

    //记录操作时间：ThreadLocal 防止并发问题
    ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    @Before("@annotation(opeAnnotation)")
    public void doBefore(JoinPoint joinPoint,OpeAnnotation opeAnnotation){
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        log.info("OperationAspect.doBefore()");
        ServletRequestAttributes attributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + request.getRemoteAddr());
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName()+ "." + joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        //获取所有参数方法一：
        log.info(getParamValueByRequest(request));
    }

    @AfterReturning("@annotation(opeAnnotation)")
    public void doAfterReturning(JoinPoint joinPoint,OpeAnnotation opeAnnotation) {
        String methodName = joinPoint.getSignature().getName();
        log.info("OperationAspect.doAfterReturning();执行方法："+methodName);
        log.info("耗时（毫秒） : " + (System.currentTimeMillis()- startTime.get()));
    }

    @Around("@annotation(opeAnnotation)")
    public Object doAroundReturning(ProceedingJoinPoint pjp, OpeAnnotation opeAnnotation) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object proceed = null;
        if(checkDoBefore(request)||"test20200323".toUpperCase().equals(adminEnvironmentValue.toUpperCase())) {
            try {
                proceed = pjp.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }else{
            throw new BizException("登录出现异常请稍候重试!");
        }
        //模块
        String modulecode = opeAnnotation.modelName().getCode() + "";
        //操作类型
        String typeStr = opeAnnotation.typeName().getDesc();
        //操作描述
        String opeDesc = opeAnnotation.opeDesc();
        //请求参数
        String requestParamsJSON = getRequestParams(pjp);
        //响应参数
        String responseParams = getResponseParams(proceed);
        //结果集
        String resultJSON = getResult(proceed);
        //日志级别
        String logLevel = opeAnnotation.logLevel() + "";
        String currentUserId = getCurrentUserId(request,proceed,typeStr);
        String userName = getCurrentUserLoginName(request,pjp,typeStr);
        String getConvertToOpeDesc = convertToOpeDesc(request,typeStr,opeDesc,proceed);
        String[] params = new String[]{modulecode, typeStr, currentUserId, userName, requestParamsJSON, responseParams, resultJSON, logLevel, getConvertToOpeDesc};
        ThreadPoolProxySingle.getThreadPoolProxy().submitRunnable(new OperationLogRunnable(operationLogService, params));
        return proceed;
    }

    private boolean checkDoBefore(HttpServletRequest request) {
        String ipAddress = SystemUtils.getIpAddress(request);
        Integer failNumValue = redisClient.get(ipAddress);
        String nonRestrictionIp = redisClient.get("ADMIN_LOGIN_RESTRICT_IP_NUM");
        if(null!=nonRestrictionIp && nonRestrictionIp.indexOf(ipAddress)>-1){
            return true;
        }
        if(null!=failNumValue&&failNumValue.intValue()>=10){
            return false;
        }
        return true;
    }

    private String convertToOpeDesc(HttpServletRequest request,String typeStr,String opeDesc,Object proceed) {
        ResponseResult responseResult = (ResponseResult) proceed;
        //获取操作成功还是失败
        String resultMsg = responseResult.getCode().intValue()==ErrorInfoEnum.SUCCESS.getCode().intValue()?"成功":"失败";
        if(OperationTypeEnum.SYSTEM_LOGIN.getDesc().equals(typeStr)) {
            String osNameAndBrowserName = SystemUtils.getRequestOsNameAndBrowserName(request);
            String ipAddress = SystemUtils.getIpAddress(request);
            String desc = opeDesc + resultMsg + " 终端：" + osNameAndBrowserName + "-【登录IP：" + ipAddress+"】";
            //失败就记录下，某个时间端不让请求频繁
            if(resultMsg.equals("失败")){
                adminLuaScriptRedisService.doIncr(ipAddress,24*60*60L);
            }
            return desc;
        }else{
            String desc = opeDesc + resultMsg;
            return desc;
        }
    }


    private String getCurrentUserLoginName(HttpServletRequest request,ProceedingJoinPoint proceedingJoinPoint,String typeStr) {
        Object[] arguments = proceedingJoinPoint.getArgs();
        if(typeStr.equals(OperationTypeEnum.SYSTEM_LOGIN.getDesc())
                || typeStr.equals(OperationTypeEnum.SEND_EMAIL_CODE.getDesc())){
            String jsonStr = JSONObject.toJSONString(arguments[1]);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            return jsonObject.get("username")==null?"-1":jsonObject.getString("username");
        }else{
            if(null==jwtUtil.getToken(request)){ return  "-1";}
            return jwtUtil.getUserName(request);
        }
    }

    private String getCurrentUserId(HttpServletRequest request,Object proceed,String typeStr) {
        if(null==jwtUtil.getToken(request)
                &&(typeStr.equals(OperationTypeEnum.SYSTEM_LOGIN.getDesc())
                || typeStr.equals(OperationTypeEnum.SEND_EMAIL_CODE.getDesc()))){
            return ((ResponseResult) proceed).getResult()==null?"-1":((AdminUserRes) ((ResponseResult) proceed).getResult()).getUserId().toString();
        }else {return jwtUtil.getUserId(request) + "";}
    }

    private String getResponseParams(Object proceed) {
        ResponseResult responseResult = (ResponseResult) proceed;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", responseResult.getCode());
        jsonObject.put("msg", responseResult.getMsg());
        if (null != responseResult.getResult()) {
            jsonObject.put("result", responseResult.getResult());
        }
        return JSONObject.toJSONString(jsonObject);
    }


    private String getResult(Object proceed) {
        ResponseResult resultBody = (ResponseResult) proceed;
        JSONObject jsonObject = new JSONObject();
        if (null != resultBody.getResult()) {
            jsonObject.put("result", resultBody.getResult());
        }
        return JSONObject.toJSONString(jsonObject);
    }

    public String getRequestParams(ProceedingJoinPoint proceedingJoinPoint) {
        JSONObject jsonObject = new JSONObject();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        createOperatorLogParamsValue(request);
        Enumeration<String> enu = request.getParameterNames();
        if (null != enu) {
            while (enu.hasMoreElements()) {
                String paraName = (String) enu.nextElement();
                jsonObject.put(paraName, request.getParameter(paraName));
            }
        } else {
            jsonObject.put("key", "没有传入参数");
        }
        if(0==jsonObject.size()){
            return getMethodParams(proceedingJoinPoint);
        }
        return JSONObject.toJSONString(jsonObject);
    }

    public String getMethodParams(ProceedingJoinPoint pjp){
        Object[] arguments = pjp.getArgs();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0;i<arguments.length;i++){
            if(arguments[i] instanceof HttpServletRequest){ continue;}
            String jsonStr = JSONObject.toJSONString(arguments[i]);
            try{
                int indexOf = jsonStr.indexOf("{");
                if(indexOf<0) {
                    stringBuffer.append(jsonStr);
                }else {
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    if (jsonObject.keySet().contains("password")) {
                        jsonObject.remove("password"); //不存密码请求信息
                    }
                    stringBuffer.append(jsonObject.toJSONString());
                }
            }catch (Exception e){
                log.error("报错字符串:{};next for:{},",jsonStr,e.toString(),e);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 组装记录操作日志参数方法
     * @param request
     * @return
     * @throws Throwable
     */
    private Map<String,String> createOperatorLogParamsValue(HttpServletRequest request){
        log.info("WebLogAspect.doAfterReturning()");
//        log.info("耗时（毫秒） : " + (System.currentTimeMillis()- startTime.get()));
        Map<String,String> paramsValue = new HashMap<String,String>();

        StringBuffer buffer = new StringBuffer();
        log.info("获取记录日志请求方法全参数："+buffer.toString());
        String returnStr = buffer.append("]").toString();
        if(returnStr.length()>500){
            returnStr = returnStr.substring(0,500);
        }
        buffer.append(getParamValueByRequest(request));
        paramsValue.put("bizFlag",returnStr);
        return paramsValue;
    }

    /**
     * 从request获取所有请求参数
     * @param request
     * @return
     */
    private String getParamValueByRequest(HttpServletRequest request){
        Enumeration<String> enu=request.getParameterNames();
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        while(enu.hasMoreElements()){
            String paraName=(String)enu.nextElement();
            //包含密码的信息不打印到日志中
            if(paraName.contains("password")){
                continue;
            }
            buffer.append(paraName).append(" : ").append(request.getParameter(paraName)).append(",");
        }
        log.info("获取记录日志请求方法全参数："+buffer.toString());
        String returnStr = buffer.toString();
        if(returnStr.length()>500){
            returnStr = buffer.toString().substring(0,500);
        }
        return returnStr;
    }
}


