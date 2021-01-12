package com.xinlian.member.biz.springrunner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TProvinceCityMapper;
import com.xinlian.biz.model.TProvinceCity;
import com.xinlian.common.response.CityRes;
import com.xinlian.common.response.ProvinceRedisRes;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;

/**
 * 应用启动时 将完整的省份城市信息存储到redis中供后续使用
 * 
 * @author lvbowen
 *
 */
@Component
@Order(1)
public class ProvinceCityLoadInRedisRunner implements ApplicationRunner {
	private final Logger logger = LoggerFactory.getLogger(ProvinceCityLoadInRedisRunner.class);

	@Autowired
	private TProvinceCityMapper provinceCityMapper;
	@Autowired
	private RedisClient redisClient;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		logger.info("ProvinceCityLoadInRedisRunner start ........");
		Long start = System.currentTimeMillis();

		try {
			List<ProvinceRedisRes> list = redisClient
					.get(RedisConstant.APP_REDIS_PREFIX + RedisConstant.PROVINCE_CITY_KEY);
			if (CollectionUtils.isEmpty(list)) {// redis为空
				// 省份信息列表
				List<TProvinceCity> provinceList = provinceCityMapper
						.selectList(new EntityWrapper<TProvinceCity>().eq("parent_code", 0));
				if (CollectionUtils.isNotEmpty(provinceList)) {
					list = new ArrayList<>(provinceList.size());
					for (TProvinceCity tProvinceCity : provinceList) {
						ProvinceRedisRes provinceRedisRes = tProvinceCity.provinceRedisRes();
						// 组合城市信息
						List<CityRes> cityResList = new ArrayList<>();
						List<TProvinceCity> cityList = provinceCityMapper.selectList(new EntityWrapper<TProvinceCity>()
								.eq("parent_code", tProvinceCity.getProvinceCityCode()));
						cityList.forEach(city -> cityResList.add(city.cityRes()));
						provinceRedisRes.setCityResList(cityResList);
						list.add(provinceRedisRes);
					}
					redisClient.set(RedisConstant.APP_REDIS_PREFIX + RedisConstant.PROVINCE_CITY_KEY, list);
				}
			}
		} catch (Exception e) {
			logger.error("ProvinceCityLoadInRedisRunner e = " + e.getMessage());
		}

		Long end = System.currentTimeMillis();
		logger.info("ProvinceCityLoadInRedisRunner end ........ 耗时 " + (end - start) + " 毫秒");
	}
}
