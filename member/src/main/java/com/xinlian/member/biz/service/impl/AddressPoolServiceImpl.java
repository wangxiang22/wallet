package com.xinlian.member.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.AddressReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.biz.dao.TAddressPoolMapper;
import com.xinlian.biz.model.TAddressPool;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IAddressPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressPoolServiceImpl implements IAddressPoolService {

    @Autowired
    private TAddressPoolMapper tAddressPoolMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public ResponseResult<List<TAddressPool>> findTAddressPool(Integer status){
        ResponseResult<List<TAddressPool>> result = new ResponseResult<>();
        EntityWrapper<TAddressPool> wrapper = new  EntityWrapper<>();
        wrapper.eq("status", status);
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(tAddressPoolMapper.selectList(wrapper));
        return result;
    }

    @Override
    public PageResult<List<TAddressPool>> findTAddressPoolPage(AddressReq req){
        PageResult<List<TAddressPool>> result = new  PageResult<>();
        EntityWrapper<TAddressPool> wrapper = new  EntityWrapper<>();
        wrapper.eq("status", req.getStatus());
        //查询总记录数
        result.setTotal(tAddressPoolMapper.selectCount(wrapper));
        //查询数据
        wrapper.last("limit " + req.pickUpOffset() + "," + req.pickUpPageSize());
        result.setResult(tAddressPoolMapper.selectList(wrapper));
        result.setCurPage(req.pickUpCurPage());
        result.setPageSize(req.pickUpPageSize());
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        return result;
    }

    /**
     * 获取 未分配的地址
     * @return
     */
    @Override
    public TAddressPool undistributed() {
        //addressPool - where id unq-address  in single table
        String redisAddressIdKey = RedisConstant.APP_REDIS_PREFIX + "ADDRESS_ID";
        String addressId = redisClient.get(redisAddressIdKey);
        TAddressPool addressPool = new TAddressPool();
        addressPool.setStatus(0);
        TAddressPool getAddressPool = null;
        if(null==addressId){
            getAddressPool = tAddressPoolMapper.getByCriteria(addressPool);
            if(null!=getAddressPool) {
                //save five hour validity
                redisClient.set(redisAddressIdKey, getAddressPool.getId().toString(), 5 * 60 * 60);
            }
        } else{
            long whereAddressId = Long.parseLong(addressId);
            addressPool.setId(whereAddressId);
            getAddressPool = tAddressPoolMapper.getByCriteria(addressPool);
            if(null!=getAddressPool && getAddressPool.getId().intValue() > (whereAddressId + 10000)){
                redisClient.set(redisAddressIdKey, getAddressPool.getId().toString(), 5 * 60 * 60);
            }
        }
        return getAddressPool;

    }

    @Override
    public int updateModel(TAddressPool addressPool){
        return tAddressPoolMapper.updateModel(addressPool);
    }

    @Override
    public int addAddressTools(TAddressPool addressPool){
        return tAddressPoolMapper.addAddressTools(addressPool);
    }

    @Override
    public int getBatchCount(){
        return tAddressPoolMapper.getBatchCount();
    }
}
