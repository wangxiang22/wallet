package com.xinlian.member.biz.jwt.util;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.xinlian.member.biz.jwt.properties.JwtPropertie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Autowired
    private JwtPropertie jwtPropertie;


    public String createToken(Long nodeId, Long id, String userAgent){
        return JWT.create()
                .withAudience(nodeId.toString(), id.toString(), userAgent)
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

    public Long getNodeId(String token){
        return Long.valueOf(JWT.decode(token).getAudience().get(0));
    }

    public Long getUserId(String token){
        return Long.valueOf(JWT.decode(token).getAudience().get(1));
    }

    public String getUserAgent(String token){
        return JWT.decode(token).getAudience().get(2);
    }

    public List<String> getUserPhoneAndId(String token){
        return JWT.decode(token).getAudience();
    }

    public Long getNodeIdCompatException(HttpServletRequest httpServletRequest,Long defNodeId){
        try{
            return getNodeId(getToken(httpServletRequest));
        }catch (Exception e){
            return defNodeId;
        }
    }

    public Long getNodeId(HttpServletRequest httpServletRequest){
        return getNodeId(getToken(httpServletRequest));
    }

    public Long getUserId(HttpServletRequest httpServletRequest){
        return getUserId(getToken(httpServletRequest));
    }

    public String getToken(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader("Authorization");
        if(token == null){
            return null;
        }
        return token.substring("Bearer ".length());
    }

}
