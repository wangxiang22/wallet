package com.xinlian.member.biz.redis;

import com.xinlian.common.cachekey.CacheTemplateInterface;

/**
 * com.xinlian.common.cachekey
 *
 * @author by Song
 * @date 2020/7/8 23:53
 */
public class CacheKeyUtils {


    public String createCacheKey(String cacheCoreValue,CacheTemplateInterface cacheTemplateInterface){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(RedisConstant.APP_REDIS_PREFIX);
        stringBuffer.append(cacheCoreValue);
        stringBuffer.append(RedisConstant.REDIS_KEY_SPLIT);
        stringBuffer.append(cacheTemplateInterface.getCode());
        return stringBuffer.toString();
    }
}
