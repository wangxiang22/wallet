package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TCurrencyManageMapper;
import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TCurrencyManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 币种管理表 服务实现类
 * </p>
 *
 * @since 2019-12-23
 */
@Service
public class TCurrencyManageServiceImpl implements TCurrencyManageService {

    @Autowired
    private TCurrencyManageMapper currencyManageMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public TCurrencyManage getCurrencyManageByCurrencyId(Long currencyId){
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "CURRENCY_" + currencyId;
        TCurrencyManage currencyManage = redisClient.get(redisKey);
        if(null==currencyManage) {
            currencyManage = currencyManageMapper.getCurrencyManageByCurrencyId(currencyId);
            redisClient.setDay(redisKey,currencyManage,14);
        }
        return currencyManage;
    }

}
