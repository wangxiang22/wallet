package com.xinlian.member.biz.jwt.properties;

import org.springframework.stereotype.Component;

@Component
public class JwtPropertie{

    private String header = "Authorization";

    private String secret = "XinlianWalletSecret";

    private Long expiration = 12*60*60*1000L;

    private String md5Key = "XinlianWalletKey";

    private String tokenPrefix = "Bearer ";

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getMd5Key() {
        return md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }
}
