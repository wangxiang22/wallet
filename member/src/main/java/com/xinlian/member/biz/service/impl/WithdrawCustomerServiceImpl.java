package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.WithdrawCustomerMapper;
import com.xinlian.biz.model.WithdrawCustomerModel;
import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.WithdrawCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Song
 * @date 2020-05-25 17:48
 * @description 容许提现usdt客户表 服务类
 */
@Service
public class WithdrawCustomerServiceImpl implements WithdrawCustomerService {

    @Autowired
    private WithdrawCustomerMapper withdrawCustomerMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public WithdrawCustomerModel getByCriteria(WithdrawCustomerModel withdrawCustomerModel){
        return withdrawCustomerMapper.getByCriteria(withdrawCustomerModel);
    }

    @Override
    public WithdrawCustomerModel judgeWithdrawCustomer(WithdrawCurrencyRequest withdrawCurrencyRequest) {
        WithdrawCustomerModel model = new WithdrawCustomerModel();
        model.setUid(withdrawCurrencyRequest.getUserId());
        model.setWithdrawStatus(1);
        model.setAllowWithdrawNum(new BigDecimal(withdrawCurrencyRequest.getNum()));
        return withdrawCustomerMapper.getByCriteria(model);
    }

    @Override
    public int updateModel(WithdrawCustomerModel model) {
        return withdrawCustomerMapper.updateModel(model);
    }

    /**
     * 或者容许的uis集合存放在redis中
     * @param redisKey
     */
    @Override
    public void initWithdrawCustomerToCache(String redisKey) {
        List<WithdrawCustomerModel> list = withdrawCustomerMapper.query();
        list.forEach( model ->{
            redisClient.hash(redisKey,model.getUid().toString(),model.getAllowWithdrawNum());
        });
    }

    @Override
    public boolean checkWithdrawCustomerUid(Long uid) {
        String redisKey = RedisConstant.WITHDRAW_CUSTOMER_UID;
        Long hlen = redisClient.hashHlen(redisKey);
        if(hlen == 0L){
            this.initWithdrawCustomerToCache(redisKey);
            return redisClient.hashHexists(redisKey,uid.toString());
        }else{
            return redisClient.hashHexists(redisKey,uid.toString());
        }
    }


}
