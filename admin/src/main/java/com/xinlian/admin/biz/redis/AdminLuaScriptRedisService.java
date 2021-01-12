package com.xinlian.admin.biz.redis;

import com.xinlian.common.result.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * com.xinlian.member.biz.redis
 *
 * @author by Song
 * @date 2020/2/10 22:27
 */
@Component
public class AdminLuaScriptRedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     *
     * @param luaScript lua脚本字符串
     * @param clazz 返回值类型
     * @param keys lua脚本用到的key集合
     * @param args lua脚本用到的key-v集合
     * @return
     */
    public <T> T doLuaScript(String luaScript, Class<T> clazz, List<String> keys, Object[] args) {
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript,Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Object result = redisTemplate.execute(redisScript,keys,args);
        if(checkResultObjectType(result,clazz)){
            return (T)result;
        }else {
            throw new BizException("出现异常");
        }
    }


    public <T> boolean checkResultObjectType(Object obj, Class<T> calzz) {
        if (obj == null) { return false; }
        try {
            T t = (T) obj;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * 执行计数
     * @param key
     * @return
     */
    public Long doIncr(String key){
        StringBuilder luaScript = new StringBuilder();
        luaScript.append("local incrNum = redis.call('incr', KEYS[1]);");
        luaScript.append("return incrNum;");
        List<String> keys = new ArrayList<>();
        keys.add(key);
        Object[] args = new Object[0];
        return this.doLuaScript(luaScript.toString(),Long.class,keys,args);
    }

    /**
     * 执行计数，并增加过期时间
     * @param key redis key
     * @param second 过期时间-秒数
     * @return
     */
    public Long doIncr(String key,Long second){
        StringBuilder luaScript = new StringBuilder();
        luaScript.append(" local incrNum = redis.call('incr',KEYS[1]);");
        luaScript.append(" local timeResidueValue = redis.call('ttl',KEYS[1]);");
        luaScript.append(" if timeResidueValue == -1 then ");
        luaScript.append(" redis.call('expire',KEYS[1],ARGV[1])");
        luaScript.append(" end;");
        luaScript.append(" return incrNum;");
        List<String> keys = new ArrayList<>();
        keys.add(key);
        Object[] args = new Object[1];
        args[0]=second;
        return this.doLuaScript(luaScript.toString(),Long.class,keys,args);
    }

    public Long deleteByLua(String key) {
        StringBuilder luaScript = new StringBuilder();
        luaScript.append("local incrNum = redis.call('del', KEYS[1]);");
        luaScript.append("return incrNum;");
        List<String> keys = new ArrayList<>();
        keys.add(key);
        Object[] args = new Object[0];
        return this.doLuaScript(luaScript.toString(),Long.class,keys,args);
    }
}
