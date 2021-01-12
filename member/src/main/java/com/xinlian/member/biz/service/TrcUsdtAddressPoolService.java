package com.xinlian.member.biz.service;

import com.xinlian.biz.dao.TrcUsdtAddressPoolMapper;
import com.xinlian.biz.model.TrcUsdtAddressPool;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrcUsdtAddressPoolService {

    @Autowired
    private TrcUsdtAddressPoolMapper trcUsdtAddressPoolMapper;
    @Autowired
    private RedisClient redisClient;

    /**
     * 获取 未分配的地址
     * @return
     */
    public TrcUsdtAddressPool undistributed() {
        //addressPool - where id unq-address  in single table
        String redisAddressIdKey = RedisConstant.APP_REDIS_PREFIX + "TRC_ADDRESS_ID";
        String addressId = redisClient.get(redisAddressIdKey);
        TrcUsdtAddressPool whereAddressPool = new TrcUsdtAddressPool();
        whereAddressPool.setStatus(1);
        TrcUsdtAddressPool getAddressPool = null;
        if(null==addressId){
            getAddressPool = trcUsdtAddressPoolMapper.getByCriteria(whereAddressPool);
            if(null!=getAddressPool) {
                //save five hour validity
                redisClient.set(redisAddressIdKey, getAddressPool.getId().toString(), 5 * 60 * 60);
            }
        } else{
            long whereAddressId = Long.parseLong(addressId);
            whereAddressPool.setId(whereAddressId);
            getAddressPool = trcUsdtAddressPoolMapper.getByCriteria(whereAddressPool);
            if(null!=getAddressPool && getAddressPool.getId().intValue() > (whereAddressId + 10000)){
                redisClient.set(redisAddressIdKey, getAddressPool.getId().toString(), 5 * 60 * 60);
            }
        }
        return getAddressPool;

    }

    public int updateModel(TrcUsdtAddressPool trcUsdtAddressPool){
        return trcUsdtAddressPoolMapper.updateModel(trcUsdtAddressPool);
    }


    public int addTrcUsdtAddressTools(TrcUsdtAddressPool trcUsdtAddressPool){
        return trcUsdtAddressPoolMapper.addTrcAddressTools(trcUsdtAddressPool);
    }

}
