package com.xinlian.biz.utils;

import com.xinlian.common.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class CommonRedisClient {
    private Logger logger = LoggerFactory.getLogger(CommonRedisClient.class);

    @Autowired
    private RedisTemplate<String, Object> template;

    /**
     * string
     */
    public void set(String key, Object value) {
        template.opsForValue().set(key, value);
    }

    /**
     * string expire
     */
    public void set(String key, Object value, long min) {
        template.opsForValue().set(key, value, min, TimeUnit.SECONDS);
    }
    public void setDay(String key,Object value,long min){
        template.opsForValue().set(key,value,min,TimeUnit.DAYS);
    }

    public <T> T get(String key){
        return (T)template.opsForValue().get(key);
    }

    /**
     * hash
     */
    public void hash(String key, Object hashKey, Object value) {
        template.opsForHash().put(key, hashKey, value);
    }

    /**
     * hashAll
     */
    public void hashAll(String key, Map map) {
        template.opsForHash().putAll(key, map);
    }

    /**
     * hash is exists
     */
    public boolean hashIsExists(String key) {
        Set set = template.opsForHash().keys(key);
        return 0==set.size() || null==set;
    }

    /**
     * set
     */
    public long pushSet(String key, Object... values) {
        Long res = template.opsForSet().add(key, values);
        return res == null ? 0 : res.longValue();
    }

    /**
     * 判断set中存不存在某个值
     */
    public boolean isMemberSet(String key, Object v){
        return template.opsForSet().isMember(key, v);
    }


    /**
     * get set
     */
    public <T> Set<T> getSet(String key) {
        Set<Object> objectSet = template.opsForSet().members(key);
        if(objectSet == null){
            return new HashSet<T>(0);
        }
        Set<T> set = new HashSet<T>(objectSet.size());
        objectSet.stream().forEach(e -> {
            set.add((T) e);
        });
        return set;
    }

    /**
     * clean set
     */
    public long emptySet(String key) {
        Long res =  template.opsForSet().remove(key, template.opsForSet().members(key).toArray());
        return res == null ? 0 : res.longValue();
    }

    /**
     *  list
     */
    public long pushList(String key, Object value) {
        Long res = template.opsForList().rightPush(key, value);
        return res == null ? 0 : res.longValue();
    }

    /**
     * get list
     */
    public <T> List<T> pullList(String key) {
        ListOperations<String, Object> opsForList = template.opsForList();
        long size = opsForList.size(key);
        List<T> list = new ArrayList<T>((int)size);
        for (int i = 0; i < size; i++) {
            list.add((T)opsForList.index(key, i));
        }
        return list;
    }

    /**
     * hash keys
     */
    public <T> Set<T> hashKeys(String key) {
        Set<Object> objectSet = template.opsForHash().keys(key);
        if(objectSet == null){
            return new HashSet<T>(0);
        }
        Set<T> set = new HashSet<T>(objectSet.size());
        objectSet.stream().forEach(e -> {
            set.add((T) e);
        });
        return set;
    }

    /**
     * hash values
     */
    public <T> List<T> hashValues(String key) {
        List<Object> objectList = template.opsForHash().values(key);
        if(objectList == null){
            return new ArrayList<>(0);
        }
        List<T> list = new ArrayList<T>(objectList.size());
        objectList.stream().forEach(e -> {
            list.add((T) e);
        });
        return list;
    }

    public <T> T getHash(String key, Object hashKey) {
        HashOperations<String, Object, Object> opsForHash = template.opsForHash();
        if(opsForHash.hasKey(key, hashKey)){
            return (T)opsForHash.get(key, hashKey);
        }
        return null;
    }

    /**
     * 移除 hash里的 key
     */
    public long removeHashKeys(String key, Object... hashKeys) {
        Long res = template.opsForHash().delete(key, hashKeys);
        return res == null ? 0 : res.longValue();
    }

    /**
     * delete key
     */
    public boolean deleteByKey(String key) {
        return template.delete(key);
    }

    /**
     * expire key
     */
    public boolean expire(String key, int time) {
        return template.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * expire
     */
    public boolean expireByteKey(byte[] key, long seconds) {
        try{
            RedisConnection redisConnection = template.getConnectionFactory().getConnection();
            return redisConnection.expire(key, seconds);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException("expireByteKey exception");
        }
    }

    /**
     * delete key
     */
   /* public int deleteKey(String key){
        RedisConnection redisConnection = template.getConnectionFactory().getConnection();
        RedisSerializer serializer =  template.getKeySerializer();
        Long res = redisConnection.del(serializer.serialize(key));
        return res == null ? 0 : res.intValue();
    }*/

    public boolean exists(String key){
        RedisConnection redisConnection = template.getConnectionFactory().getConnection();
        return redisConnection.exists(key.getBytes());
    }

    /**
     * set 求差集
     */
    public Set<Object> sDiff(String key, String otherKey){
        return template.opsForSet().difference(key, otherKey);
    }

    /**
     * set 求并集
     */
    public Set<Object> sUnion(String key, String otherKey){
        return template.opsForSet().union(key, otherKey);
    }

    /**
     * 替换 list中的某个位置的值
     */
    public void replaceListValue(String key, int index, Object v){
        template.opsForList().set(key, index, v);
    }

    public boolean setNx(String key, Object value){
        RedisConnection redisConnection = template.getConnectionFactory().getConnection();
        RedisSerializer keySerializer = template.getKeySerializer();
        RedisSerializer valueSerializer = template.getValueSerializer();
        return redisConnection.setNX(keySerializer.serialize(key), valueSerializer.serialize(value));
    }

    public RedisSerializer getKeySerializer(){
        return template.getKeySerializer();
    }

    public RedisSerializer getValueSerializer(){
        return template.getValueSerializer();
    }


    public Long rightPushList(String key, Object value){
        return template.opsForList().rightPush(key, value);
    }

    public <T> T rightPopList(String key){
        return (T) template.opsForList().rightPop(key);
    }

    public <T> T lastOneList(String key){
        Long size = template.opsForList().size(key);
        if(size==null || size.longValue()==0){
            return null;
        }
        return (T) template.opsForList().index(key, size-1);
    }

    public void setList(String key, long index, Object value){
        template.opsForList().set(key, index, value);
    }

    public Set<String> getKeys(String prefix){
        return template.keys(prefix);
    }

    public Long setSize(String key){
        return template.opsForSet().size(key);
    }

    public Long removeSetValue(String key, Object value){
        return template.opsForSet().remove(key, value);
    }

    public Long increment(String key, long add){
        return template.opsForValue().increment(key, add);
    }

    public Long listRemove(String key, Object v){
        return template.opsForList().remove(key,1, v);
    }

    public RedisTemplate<String, Object> template(){
        return template;
    }

    public Long generateInc(String key) {
        RedisAtomicLong counter = new RedisAtomicLong(key, template.getConnectionFactory());
        return counter.incrementAndGet();
    }

    /**
     * 插入值，默认更新时间是当天23:59:59的剩余秒数
     * @param key
     * @param value
     */
    public void setDayResidueTimes(String key,Object value){
        long residueTimesSeconds = CommonUtil.getTheDayResidueSecond();
        this.set(key,value,residueTimesSeconds);
    }
}
