package com.xinlian.admin.biz.jwt.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.xinlian.admin.biz.jwt.properties.JwtPropertie;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.server.vo.UserLoginSession;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Autowired
    private JwtPropertie jwtPropertie;
    @Autowired
    private RedisClient redisClient;

    public String createToken(Long id, String userAgent,String reqTimestamp){
        return JWT.create()
                .withAudience(id.toString(), userAgent,reqTimestamp)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtPropertie.getExpiration()))
                .sign(Algorithm.HMAC256(jwtPropertie.getMd5Key()));
    }

    public boolean verifyToken(String token){
        try{
            JWT.require(Algorithm.HMAC256(jwtPropertie.getMd5Key())).build().verify(token);
            return true;
        }catch(JWTVerificationException e){
            return false;
        }
    }

    public boolean isOvertime(String token){
        Date date = JWT.decode(token).getExpiresAt();
        if(date == null){
            return false;
        }
        return date.before(new Date());
    }

    public Long getUserId(String token){
        return Long.valueOf(JWT.decode(token).getAudience().get(0));
    }

    public String getUserAgent(String token){
        return JWT.decode(token).getAudience().get(1);
    }

    public Long getUserId(HttpServletRequest httpServletRequest){
        return getUserId(getToken(httpServletRequest));
    }

    public String getUserName(HttpServletRequest httpServletRequest){
        String md5Token = this.getMd5Token(httpServletRequest);
        UserLoginSession userLoginSession =  redisClient.get(md5Token);
        if(null==userLoginSession){throw new BizException("请重新登录!");}
        return userLoginSession.getAdminUserRes().getUsername();
    }

    public String getToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Authorization");
        if(token == null){
            return null;
        }
        return token.substring("Bearer ".length());
    }

    public String getMd5Token(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Authorization");
        if(token == null){
            return null;
        }
        token = token.substring("Bearer ".length());
        return DigestUtils.md5Hex(token);
    }

    public UserLoginSession getUserLoginSession(HttpServletRequest httpServletRequest){
        String md5Token = this.getMd5Token(httpServletRequest);
        UserLoginSession userLoginSession =  redisClient.get(md5Token);
        return userLoginSession;
    }
}
