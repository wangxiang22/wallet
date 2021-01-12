package com.xinlian.member.biz.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.biz.dao.AdminUpdateVersionInfoMapper;
import com.xinlian.biz.dao.TUpdateVersionMapper;
import com.xinlian.biz.model.AdminUpdateVersionInfo;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TUpdateVersionService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author WX
 * @since 2020-04-28
 */
@Configuration
@EnableScheduling
@Service
@Slf4j
public class TUpdateVersionServiceImpl extends ServiceImpl<TUpdateVersionMapper, TUpdateVersion>
		implements TUpdateVersionService {

	private final Logger logger = LoggerFactory.getLogger(TNewsArticleServiceImpl.class);

	@Autowired
	private TUpdateVersionMapper updateVersionMapper;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private AdminUpdateVersionInfoMapper adminUpdateVersionInfoMapper;
	@Autowired
	private RedisLockRegistry redisLockRegistry;

	public ResponseResult test(TUpdateVersion updateVersion) {
		ResponseResult result = new ResponseResult();
		if (null == updateVersion) {
			result.setMsg("暂无新版本");
			result.setCode(200);
			return result;
		}

		long time = System.currentTimeMillis() / 1000;
		Long startTime = updateVersion.getStartTime();
		Long endTime = updateVersion.getEndTime();
		if (time < startTime) {
			result.setMsg("暂无新版本");
			result.setCode(200);
			return result;
		}
		if (time >= startTime && time < endTime) {
			result.setMsg("请求成功");
			result.setCode(200);
			result.setResult(updateVersion);
			return result;
		}
		if (time > endTime) {
			updateVersionMapper.updateStatus(updateVersion.getId());
			if (null != redisTemplate.keys(RedisConstant.APP_VERSION)) {
				redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
			}
			result.setMsg("暂无新版本");
			result.setCode(200);
			return result;
		}
		return result;
	}

	@Transactional
	@Override
	public ResponseResult queryVersion(VersionDataReq versionReq) {
		ResponseResult result = new ResponseResult();
		String appVersion = versionReq.getType() + RedisConstant.APP_VERSION;
		TUpdateVersion tUpdateVersion = redisClient.get(appVersion);
		if (null == tUpdateVersion) {
			try {
				tUpdateVersion = updateVersionMapper.queryVersionLimit1(versionReq.getType());
				if (null == tUpdateVersion) {
					result.setMsg("暂无新版本");
					result.setCode(200);
					return result;
				}
				redisClient.set(appVersion, tUpdateVersion);
			} catch (Exception e) {
				logger.info(e.toString());
				result.setMsg("请求超时");
				result.setCode(400);
				return result;
			}
		}

		return test(tUpdateVersion);
	}

	@Override
	public TUpdateVersion queryVersion(Integer type) {
		String appVersion = type + RedisConstant.APP_VERSION;
		TUpdateVersion tUpdateVersion = redisClient.get(appVersion);
		if (null == tUpdateVersion) {
			try {
				tUpdateVersion = updateVersionMapper.queryVersionLimit1(type);
				redisClient.set(appVersion, tUpdateVersion);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		return tUpdateVersion;
	}

	// 定时更新数据库中的推送数量
	@Override
	@Scheduled(cron = "50 59 23 * * ? ")
	public ResponseResult updateIssuedToSql() {
		ResponseResult<Object> result = new ResponseResult<>();
		long time = System.currentTimeMillis() / 1000;
		AdminUpdateVersionInfo versionInfo = new AdminUpdateVersionInfo();
		String keyVersion = "VERSION_KEY_ISSUED";
		// 分布式锁
		// 两台服务器只插入一台的记录
		Lock lock = redisLockRegistry.obtain(keyVersion);
		boolean redisFlag = true;
		try {
			if (!lock.tryLock()) {
				redisFlag = false;
				log.warn(Thread.currentThread().getName() + " : wxVersion！");
				return null;
			}
			Set keys = redisTemplate.keys(RedisConstant.VERSION_COUNT_ISSUED + "*");
			Set keysInstall = redisTemplate.keys(RedisConstant.VERSION_COUNT_INSTALL + "*");
			if (keys.size() == 0 || keysInstall.size() == 0) {
				List<TUpdateVersion> tUpdateVersions = updateVersionMapper.queryVer();
				if (null == tUpdateVersions || tUpdateVersions.size() == 0) {
					result.setMsg("暂无更新");
					return result;
				}
				for (TUpdateVersion tUpdateVersion : tUpdateVersions) {
					Integer id = tUpdateVersion.getId();
					Integer type = tUpdateVersion.getType();
					versionInfo.setIssued(0);
					versionInfo.setDownload(0);
					versionInfo.setTime(time);
					versionInfo.setVid(id);
					versionInfo.setType(type);
					adminUpdateVersionInfoMapper.insert(versionInfo);
				}
				result.setMsg("key");
				result.setCode(200);
				return result;
			}
			for (Object key : keys) {
				Long num = redisClient.hashHlen(key.toString());
				String[] split = key.toString().split(RedisKeys.SYMBOLIC_LINK);
				String s = split[1];
				String s1 = split[2];
				int type = Integer.parseInt(s1);
				int vid = Integer.parseInt(s);
				versionInfo.setVid(vid);
				versionInfo.setType(type);
				versionInfo.setTime(time);
				versionInfo.setIssued(num.intValue());
				adminUpdateVersionInfoMapper.insert(versionInfo);
			}
			for (Object install : keysInstall) {
				Long num = redisClient.hashHlen(install.toString());
				String[] split = install.toString().split(RedisKeys.SYMBOLIC_LINK);
				String s = split[1];
				String s1 = split[2];
				int type = Integer.parseInt(s1);
				int vid = Integer.parseInt(s);
				versionInfo.setVid(vid);
				versionInfo.setType(type);
				versionInfo.setTime(time);
				versionInfo.setDownload(num.intValue());
				adminUpdateVersionInfoMapper.updateDownload(versionInfo);
			}
			result.setMsg("请求成功");
			result.setCode(200);
			return result;
		} catch (Exception e) {
			logger.error("统计异常:{}", e.toString(), e);
			result.setMsg("请求超时");
			result.setCode(400);
			return result;
		} finally {
			if (redisFlag) {
				lock.unlock();
			}
		}
	}

	/*
	 * //定时更新数据库中的下载数量
	 * 
	 * @Scheduled(cron = "59 59 23 * * ? ") public ResponseResult
	 * updateInstallToSql(){ ResponseResult<Object> result = new ResponseResult<>();
	 * long l = System.currentTimeMillis()/1000; AdminUpdateVersionInfo versionInfo
	 * = new AdminUpdateVersionInfo(); String keyVer="VERSION_KEY_DOWNLOAD"; Lock
	 * lock = redisLockRegistry.obtain(keyVer); boolean redisFlag = true; try {
	 * if(!lock.tryLock()){ redisFlag = false;
	 * log.warn(Thread.currentThread().getName() + " : wxVersion！"); return null; }
	 * Set keys = redisTemplate.keys("*"+RedisConstant.VERSION_COUNT_INSTALL+"*");
	 * //System.out.println(keys); if (keys.size()==0){ List<TUpdateVersion>
	 * tUpdateVersions = updateVersionMapper.queryVer(); if (null==tUpdateVersions
	 * || tUpdateVersions.size()==0){ result.setMsg("暂无更新"); return result; } for
	 * (TUpdateVersion tUpdateVersion : tUpdateVersions) { Integer id =
	 * tUpdateVersion.getId(); Integer type = tUpdateVersion.getType();
	 * versionInfo.setDownload(0); versionInfo.setTime(l); versionInfo.setVid(id);
	 * versionInfo.setType(type); adminUpdateVersionInfoMapper.insert(versionInfo);
	 * } result.setMsg("请求成功"); result.setCode(200); return result; } for (Object
	 * key : keys) { Integer num = redisClient.get(key.toString()); String[] split =
	 * key.toString().split("_:"); List<String> collect =
	 * Arrays.stream(split).collect(Collectors.toList()); String s = collect.get(0);
	 * String s1 = collect.get(2); int type = Integer.parseInt(s1); int vid =
	 * Integer.parseInt(s); versionInfo.setVid(vid); versionInfo.setType(type);
	 * versionInfo.setTime(l); versionInfo.setDownload(num);
	 * adminUpdateVersionInfoMapper.insert(versionInfo); } result.setMsg("请求成功");
	 * result.setCode(200); return result; }catch (Exception e){
	 * logger.info(e.toString()); result.setMsg("请求超时"); result.setCode(400); return
	 * result; }finally { if(redisFlag) { lock.unlock(); } } }
	 */
}
