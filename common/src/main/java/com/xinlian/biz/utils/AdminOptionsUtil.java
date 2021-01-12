package com.xinlian.biz.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.AdminOptionsMapper;
import com.xinlian.biz.model.AdminOptions;
import com.xinlian.common.redis.RedisKeys;

import lombok.extern.slf4j.Slf4j;

/**
 * com.xinlian.biz.utils
 *
 * @author by Song
 * @date 2020/3/18 23:54
 */
@Slf4j
@Component
public class AdminOptionsUtil {

	@Autowired
	private CommonRedisClient commonRedisClient;
	@Autowired
	private AdminOptionsMapper adminOptionsMapper;

	/**
	 *
	 * @param belongsSystemCode 配置项所属code
	 * @param entityClass       需要转换类.class
	 * @param <T>               类对象
	 * @return
	 * @throws Exception
	 */
	public <T> T fieldEntityObject(String belongsSystemCode, Class<T> entityClass) throws Exception {
		try {
			// 判断是否有缓存
			String redisKey = RedisKeys.REDIS_KEY_SESSION_ADMIN_OPTION + belongsSystemCode;
			T getEntityObj = commonRedisClient.get(redisKey);
			if (null != getEntityObj) {
				return getEntityObj;
			}
			T entity = entityClass.newInstance();
			AdminOptions adminOptions = new AdminOptions();
			adminOptions.setBelongsSystemCode(belongsSystemCode);
			adminOptions.setIsShow(1);
			List<AdminOptions> adminOptionsList = adminOptionsMapper.queryByBelongsSystemCode(adminOptions);
			for (AdminOptions getModel : adminOptionsList) {
				// 通过反射赋值
				for (Field field : entityClass.getDeclaredFields()) {
					if (field.getName().equals(getModel.getOptionName())) {
						field.setAccessible(true);
						field.set(entity, getModel.getOptionValue());
					}
				}
			}
			commonRedisClient.set(redisKey, entity);
			log.info("读取配置文件信息：redisKey:{},redisValue:{}", redisKey, JSONObject.toJSONString(entity));
			return entity;
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}

	/**
	 * 获取单独一个配置项的value值
	 * 
	 * @param belongsSystemCode 配置项所属code（这个code只有一个的情况才适用）
	 * @return 配置项value值
	 */
	public String findAdminOptionOne(String belongsSystemCode) {
		try {
			String redisKey = RedisKeys.REDIS_KEY_SESSION_ADMIN_OPTION + belongsSystemCode;
			String redisValue = commonRedisClient.get(redisKey);
			if (StringUtils.isNotBlank(redisValue)) {
				return redisValue;
			}

			AdminOptions adminOptions = adminOptionsMapper
					.queryByBelongsSystemCodeLimit1(new AdminOptions(belongsSystemCode));
			if (null != adminOptions && StringUtils.isNotBlank(adminOptions.getOptionValue())) {
				redisValue = adminOptions.getOptionValue();
				commonRedisClient.set(redisKey, redisValue);
				return redisValue;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return null;
	}
}
