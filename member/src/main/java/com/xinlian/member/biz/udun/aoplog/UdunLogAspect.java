package com.xinlian.member.biz.udun.aoplog;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.Address;
import com.xinlian.biz.model.TUdunRequestLog;
import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TUdunRequestLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Enumeration;

@Aspect
@Component
@Slf4j
public class UdunLogAspect {

    @Autowired
    private TUdunRequestLogService udunRequestLogService;
    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(udunLogAnnotation)")
    public Object doAfterReturning(ProceedingJoinPoint joinPoint, UdunLogAnnotation udunLogAnnotation) {
        Object proceed = null;
        try {
            proceed = joinPoint.proceed();
        } catch (BizException e) {
            log.error("error {}",e.getMsg(),e);
        } catch (Exception e) {
            log.error("error {}",e.getMessage(),e);
        } catch (Throwable throwable) {
            log.error("error {}",throwable.getMessage(),throwable);
        }
        Object[] objs = joinPoint.getArgs();
        //模块
        //操作类型
        //请求参数
        String requestParamsJSON = getRequestParams(objs);
        //响应参数
        String responseParams = getResponseParams(proceed);
        Long currentUserId = getCurrentUserId(udunLogAnnotation.udunOpeType());
        TUdunRequestLog udunRequestLog = this.createUdunRequestLogModel(requestParamsJSON,responseParams,udunLogAnnotation.udunOpeType());
        udunRequestLog.setUid(currentUserId);
        //记录日志
        new Thread(()->{
            udunRequestLogService.saveModel(udunRequestLog);
        }).start();
        return proceed;
    }

    private TUdunRequestLog createUdunRequestLogModel(String requestParamsJSON, String responseParams, String udunOpeType) {
        TUdunRequestLog udunRequestLog = new TUdunRequestLog();
        String replaceResultStr = requestParamsJSON.replaceAll("\\\\","");
        udunRequestLog.setRequestBody(replaceResultStr);
        udunRequestLog.setResponseBody(responseParams);
        udunRequestLog.setUdunOpeType(udunOpeType);
        return udunRequestLog;
    }

    private Long getCurrentUserId(String typeStr) {
        if("系统发起提币接口".equals(typeStr)){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            try {
                return jwtUtil.getUserId(request);
            }catch (Exception e){
                return -1L;
            }
        }
        return -1L;
    }

    private String getResponseParams(@NotNull Object proceed) {
        if(proceed==null){return "返回参数异常!";}
        if(proceed instanceof Address){
            return JSONObject.toJSONString(proceed);
        }
        if(proceed instanceof String){return proceed.toString();}
        if(proceed instanceof JSONObject){return ((JSONObject) proceed).toJSONString();}
        ResponseResult resultBody = (ResponseResult) proceed;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", resultBody.getCode());
        jsonObject.put("msg", resultBody.getMsg());
        if (null != resultBody.getResult()) {
            jsonObject.put("result", resultBody.getResult());
        }
        return JSONObject.toJSONString(jsonObject);
    }

    public String getRequestParams(Object [] objs) {
        if(null==objs){
            return "无请求参数!";
        }
        if(objs[0]!=null && objs[0] instanceof WithdrawCurrencyRequest){
            String removeKey = "deal_psw";
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(objs[0]));
            if(jsonObject.containsKey(removeKey)){jsonObject.remove(removeKey);}
            if(jsonObject.containsKey("uid")){jsonObject.remove("uid");}
            return jsonObject.toJSONString();
        }
        JSONObject returnJson = new JSONObject();
        for(int i=0;i<objs.length;i++){
            if(objs[i] instanceof String ){
                returnJson.put("param"+i,objs[i]);
                continue;
            }
            if(objs[i] instanceof Object) {
                try {
                    returnJson.put("param"+i,JSONObject.toJSONString(objs[i]));
                }catch (Exception e){
                }
            }
        }
        if(returnJson.size()!=0){return returnJson.toJSONString();}
        JSONObject jsonObject = new JSONObject();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null==attributes){return "没有参数!";}
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> enu = request.getParameterNames();
        if (null != enu) {
            while (enu.hasMoreElements()) {
                String paraName = (String) enu.nextElement();
                if("timestamp".equalsIgnoreCase(paraName)){
                    jsonObject.put(paraName, CommonUtil.millisecondTimeStampToFormatDate(request.getParameter(paraName),null));
                }else {
                    jsonObject.put(paraName, request.getParameter(paraName));
                }
            }
        } else {
            jsonObject.put("key", "没有传入参数");
        }
        return JSONObject.toJSONString(jsonObject);
    }

    public static void main(String[] args) {
        String de = "1579173163692";
        System.err.println( CommonUtil.millisecondTimeStampToFormatDate(de,null));
    }
}
