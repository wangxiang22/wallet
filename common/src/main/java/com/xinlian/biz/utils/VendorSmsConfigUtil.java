package com.xinlian.biz.utils;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.VendorSmsConfigMapper;
import com.xinlian.biz.model.VendorSmsConfigModel;
import com.xinlian.common.redis.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * com.xinlian.biz.utils
 *
 * @author by Song
 * @date 2020/3/18 23:54
 */
@Slf4j
@Component
public class VendorSmsConfigUtil {

    @Autowired
    private CommonRedisClient commonRedisClient;
    @Autowired
    private VendorSmsConfigMapper vendorSmsConfigMapper;

    /**
     *
     * @param belongsSystemCode 配置项所属code
     * @param entityClass 需要转换类.class
     * @param <T> 类对象
     * @return
     * @throws Exception
     */
    public <T> T fieldEntityObject(String belongsSystemCode,Class<T> entityClass)throws Exception{
        //判断是否有缓存
        String redisKey = RedisKeys.REDIS_VENDOR_SMS + belongsSystemCode;
        T getEntityObj = commonRedisClient.get(redisKey);
        if(null!=getEntityObj){
            return getEntityObj;
        }
        T entity = entityClass.newInstance();
        VendorSmsConfigModel vendorSmsConfigModel = new VendorSmsConfigModel();
        vendorSmsConfigModel.setBelongsSystemCode(belongsSystemCode);
        vendorSmsConfigModel.setIsShow(1);
        List<VendorSmsConfigModel> vendorSmsConfigModelList = vendorSmsConfigMapper.querySmsConfigSystemCode(vendorSmsConfigModel);
        for (VendorSmsConfigModel getModel : vendorSmsConfigModelList) {
            Field getField = this.getFieldByName(getModel.getOptionName(),entityClass);
            getField.setAccessible(true);
            getField.set(entity,getModel.getOptionValue());
        }
        commonRedisClient.set(redisKey,entity);
        log.info("读取配置文件信息：redisKey:{},redisValue:{}",redisKey, JSONObject.toJSONString(entity));
        return entity;
    }

    private Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();

        // 如果本类中存在该字段，则返回
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        return null;
    }



}
