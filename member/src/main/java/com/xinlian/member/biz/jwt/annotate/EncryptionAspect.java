package com.xinlian.member.biz.jwt.annotate;

import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Song
 * @date 2020-07-08 20:40
 * @description
 */
@Aspect
@Component
@Slf4j
public class EncryptionAspect {

    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Value("${isDebug}")
    private boolean isDebug;
    @Value("${swaggerAuth}")
    private String swaggerAuth;

    @Around("@annotation(encryptionAnnotation)")
    public Object doAfterReturning(ProceedingJoinPoint joinPoint, EncryptionAnnotation encryptionAnnotation) throws Throwable{
        Object [] args = joinPoint.getArgs();
        Map<String,String> map = (Map)args[0];
        try {
            //本地debug 无须解密
            if(!isDebug) {
                String stayDecodeData = map.get("data");
                log.info("~~~~~~收到的请求参数：{}",stayDecodeData);
                String privateKey = "";
                if(null!=swaggerAuth && "gray".equals(swaggerAuth)){
                    privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_GRAY_PRIVATE_KEY.getBelongsSystemCode());
                }else{
                    privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_PRIVATE_KEY.getBelongsSystemCode());
                }
                byte [] bvy = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(stayDecodeData),privateKey);
                String encryptionStr = new String(bvy);
                map.put("data",encryptionStr);
                args[0] = map;
            }else{
                args[0] = map;
            }
        }catch (Exception e){
            log.error("解密异常:{}",e.toString(),e);
            throw new BizException("网络请求异常，请稍后重试!!!");
        }
        try{
            Object result = joinPoint.proceed(args);
            return result;
        }catch (BizException e){
            log.error("APP-业务执行异常:{}",e.getMsg(),e);
            throw new BizException(e.getMsg());
        }

    }



}

