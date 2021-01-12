package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TProvinceCityMapper;
import com.xinlian.biz.model.TProvinceCity;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.CityRes;
import com.xinlian.common.response.ProvinceRedisRes;
import com.xinlian.common.response.ProvinceRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TProvinceCityService;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TProvinceCityServiceImpl implements TProvinceCityService {
    @Autowired
    private TProvinceCityMapper provinceCityMapper;
    @Autowired
    private RedisClient redisClient;

    /**
     * 将完整的省份城市信息存储到redis中读取
     * @return 省份城市组合信息列表
     */
    private List<ProvinceRedisRes> getProvinceCityList() {
        //读取缓存中省份城市组合信息
        List<ProvinceRedisRes> list = redisClient.get(RedisConstant.APP_REDIS_PREFIX + RedisConstant.PROVINCE_CITY_KEY);
        if (CollectionUtils.isEmpty(list)) {//redis为空
            //省份信息列表
            List<TProvinceCity> provinceList = provinceCityMapper.selectList(new EntityWrapper<TProvinceCity>().eq("parent_code", 0));
            if (CollectionUtils.isNotEmpty(provinceList)) {
                list = new ArrayList<>(provinceList.size());
                for (TProvinceCity tProvinceCity : provinceList) {
                    ProvinceRedisRes provinceRedisRes = tProvinceCity.provinceRedisRes();
                    //组合城市信息
                    List<CityRes> cityResList = new ArrayList<>();
                    List<TProvinceCity> cityList = provinceCityMapper.selectList(new EntityWrapper<TProvinceCity>().eq("parent_code", tProvinceCity.getProvinceCityCode()));
                    cityList.forEach(city -> cityResList.add(city.cityRes()));
                    provinceRedisRes.setCityResList(cityResList);
                    list.add(provinceRedisRes);
                }
                redisClient.set(RedisConstant.APP_REDIS_PREFIX + RedisConstant.PROVINCE_CITY_KEY,list);
            }else{
                list = new ArrayList<>();
            }
        }
        return list;
    }

    @Override
    public ResponseResult<List<ProvinceRes>> findProvinceCityByNodeId(Long nodeId) {
        ResponseResult<List<ProvinceRes>> result = new ResponseResult<>();
        List<ProvinceRedisRes> provinceCityList = getProvinceCityList();
        //返回符合条件的省市组合数据
        List<ProvinceRes> provinceCityResList = new ArrayList<>();
        for (ProvinceRedisRes provinceRedisRes : provinceCityList) {
            if (null == provinceRedisRes.getNodeId()){ continue;}
            //获取省份关联的节点id列表，判断参数是否在列表中
            Long[] nodeIds = (Long[]) ConvertUtils.convert(provinceRedisRes.getNodeId().split(","),Long[].class);
            List<Long> nodeIdList = Arrays.stream(nodeIds).collect(Collectors.toList());
            for (Long serverNodeId : nodeIdList) {
                if (serverNodeId.equals(nodeId)) {
                    ProvinceRes provinceRes = new ProvinceRes();
                    provinceRes.setProvinceCode(provinceRedisRes.getProvinceCode());
                    provinceRes.setProvinceName(provinceRedisRes.getProvinceName());
                    provinceRes.setCityResList(provinceRedisRes.getCityResList());
                    provinceCityResList.add(provinceRes);
                }
            }
        }
        result.responseResult(GlobalConstant.ResponseCode.SUCCESS,provinceCityResList);
        return result;
    }
}
