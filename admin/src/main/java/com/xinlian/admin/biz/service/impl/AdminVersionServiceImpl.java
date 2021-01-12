package com.xinlian.admin.biz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.AdminVersionService;
import com.xinlian.biz.dao.AdminUpdateVersionInfoMapper;
import com.xinlian.biz.dao.TUpdateVersionMapper;
import com.xinlian.biz.model.AdminUpdateVersionInfo;
import com.xinlian.biz.model.TUpdateVersion;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.request.VersionDataReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

@Service
public class AdminVersionServiceImpl implements AdminVersionService {

	@Autowired
	private TUpdateVersionMapper updateVersionMapper;

	@Autowired
	private AdminUpdateVersionInfoMapper adminUpdateVersionInfoMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	// 查询所有版本
	@Override
	public PageResult<List<TUpdateVersion>> queryVersion(PageReq pageReq) {
		PageResult<List<TUpdateVersion>> result = new PageResult<>();
		result.setTotal(updateVersionMapper.selectCount(null));
		result.setCurPage(pageReq.pickUpCurPage());
		result.setPageSize(pageReq.pickUpPageSize());
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setResult(updateVersionMapper.selectPage(
				new Page<TUpdateVersion>((int) pageReq.pickUpCurPage(), (int) pageReq.pickUpPageSize()),
				new EntityWrapper<TUpdateVersion>().orderBy("id", false)));
		return result;
	}

	// 查询版本统计数据，每天的下载数量等
	@Override
	public PageResult<List<AdminUpdateVersionInfo>> queryVersionData(VersionDataReq versionDataReq) {
		PageResult<List<AdminUpdateVersionInfo>> result = new PageResult<>();
		Integer vid = versionDataReq.getVid();
		Integer type = versionDataReq.getType();
		if (null == vid || null == type) {
			result.setMsg("请求参数有误");
			result.setCode(400);
			return result;
		}

		result.setTotal(adminUpdateVersionInfoMapper.selectCount(new EntityWrapper<AdminUpdateVersionInfo>()
				.eq("vid", versionDataReq.getVid()).eq("type", versionDataReq.getType())));
		result.setCurPage(versionDataReq.pickUpCurPage());
		result.setPageSize(versionDataReq.pickUpPageSize());
		result.setCode(GlobalConstant.ResponseCode.SUCCESS);
		result.setResult(adminUpdateVersionInfoMapper.selectPage(
				new Page<AdminUpdateVersionInfo>((int) versionDataReq.pickUpCurPage(),
						(int) versionDataReq.pickUpPageSize()),
				new EntityWrapper<AdminUpdateVersionInfo>().eq("vid", vid).eq("type", type).orderBy("time", false)));
		return result;
	}

	// 根据id查看版本内容
	@Override
	public ResponseResult queryVersionTime(VersionDataReq versionDataReq) {
		ResponseResult<Object> result = new ResponseResult<>();
		if (null == versionDataReq.getId() || 0 == versionDataReq.getId()) {
			result.setCode(400);
			result.setMsg("参数有误");
			return result;
		}
		TUpdateVersion tUpdateVersion = updateVersionMapper.selectById(versionDataReq.getId());
		result.setCode(200);
		result.setMsg("请求成功");
		result.setResult(tUpdateVersion);
		return result;
	}

	@Override
	public ResponseResult queryNewVersion(VersionDataReq versionReq) {
		ResponseResult<Object> result = new ResponseResult<>();
		TUpdateVersion tUpdateVersion = updateVersionMapper.queryVersionLimit1(versionReq.getType());
		if (null == tUpdateVersion) {
			result.setMsg("暂无新版本");
			return result;
		}
		result.setResult(tUpdateVersion);
		result.setMsg("请求成功");
		return result;
	}

	// 删除版本
	@Override
	public ResponseResult deleteVersion(VersionDataReq versionReq) {
		ResponseResult<Object> result = new ResponseResult<>();
		Integer integer = updateVersionMapper.deleteById(versionReq.getId());
		if (integer != 1) {
			result.setMsg("删除失败");
			return result;
		}
		result.setCode(200);
		result.setMsg("删除成功");
		result.setResult(integer);
		if (null != redisTemplate.keys(RedisConstant.APP_VERSION)) {
			redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
		}
		return result;
	}

	// 添加新版本
	@Override
	public ResponseResult addVersion(TUpdateVersion tUpdateVersion) {
		ResponseResult<Object> result = new ResponseResult<>();
		Integer status = tUpdateVersion.getStatus();
		if (null == status || 0 == status) {
			Integer num = updateVersionMapper.insert(tUpdateVersion);
			if (num == 1) {
				result.setCode(200);
				result.setMsg("添加成功");
				if (null != redisTemplate.keys(RedisConstant.APP_VERSION)) {
					redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
				}
				return result;
			}
		} else {
			long timeMillis = System.currentTimeMillis() / 1000;
			tUpdateVersion.setStartTime(timeMillis);
			Integer count = updateVersionMapper.insert(tUpdateVersion);
			if (count == 1) {
				result.setCode(200);
				result.setMsg("添加成功");
				if (null != redisTemplate.keys(RedisConstant.APP_VERSION)) {
					redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
				}
				return result;
			}
		}
		result.setCode(400);
		result.setMsg("添加失败，请稍后重试");
		return result;
	}

	// 修改版本
	@Override
	public ResponseResult updateVersion(TUpdateVersion tUpdateVersion) {
		ResponseResult<Object> result = new ResponseResult<>();
		Integer status = tUpdateVersion.getStatus();
		if (0 == status || null == status) {
			tUpdateVersion.setStartTime(0L);
			Integer integer = updateVersionMapper.updateById(tUpdateVersion);
			if (integer == 1) {
				result.setCode(200);
				result.setMsg("更新成功");
				if (null != redisTemplate.keys(RedisConstant.APP_VERSION)
						|| !"".equals(redisTemplate.keys("*" + RedisConstant.APP_VERSION))) {
					redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
					// System.out.println(redisTemplate.keys("*"+RedisConstant.APP_VERSION));
				}
				return result;
			}
		} else {
			long l = System.currentTimeMillis() / 1000;
			tUpdateVersion.setStartTime(l);
			Integer integer = updateVersionMapper.updateById(tUpdateVersion);
			if (1 == integer) {
				result.setCode(200);
				result.setMsg("更新成功");
				if (null != redisTemplate.keys(RedisConstant.APP_VERSION)) {
					redisTemplate.delete(redisTemplate.keys("*" + RedisConstant.APP_VERSION));
				}
				return result;
			}
		}
		result.setCode(400);
		result.setMsg("更新失败，请稍后重试");
		return result;
	}

}
