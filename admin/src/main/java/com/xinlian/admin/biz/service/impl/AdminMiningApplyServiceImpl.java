package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.AdminMiningApplyService;
import com.xinlian.admin.biz.service.MiningApplyOkService;
import com.xinlian.biz.dao.TMiningApplyMapper;
import com.xinlian.biz.model.MiningApplyOk;
import com.xinlian.biz.model.TMiningApply;
import com.xinlian.common.request.FindAllUserReq;
import com.xinlian.common.request.MiningConfigReq;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.xinlian.common.redis.RedisConstant.*;

@Service
public class AdminMiningApplyServiceImpl implements AdminMiningApplyService {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private MiningApplyOkService miningApplyOkService;
    @Autowired
    private TMiningApplyMapper tMiningApplyMapper;

    @Override
    public void setConfig(MiningConfigReq miningConfigReq) {
        if (miningConfigReq.getActiveNum()!=null){
            redisClient.set(MINING.concat(ACTIVE_COUNT),miningConfigReq.getActiveNum());
        }
        if (miningConfigReq.getActiveTime()!=null){
            redisClient.set(MINING.concat(ACTIVE_TIME),miningConfigReq.getActiveTime());
        }
        if (miningConfigReq.getNextActiveTime()!=null){
            redisClient.set(MINING.concat(NEXT_ACTIVE_TIME),miningConfigReq.getNextActiveTime());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passUser(TMiningApply tMiningApply) {
        if (tMiningApply.getState()==3){
            MiningApplyOk miningApplyOk = new MiningApplyOk();
            BeanUtils.copyProperties(tMiningApply,miningApplyOk);
            miningApplyOk.setPassTime(new Date());
            miningApplyOkService.insert(miningApplyOk);
            tMiningApplyMapper.update(tMiningApply,new EntityWrapper<TMiningApply>().eq("id",tMiningApply.getId()));
        }else {
            tMiningApplyMapper.update(tMiningApply,new EntityWrapper<TMiningApply>().eq("id",tMiningApply.getId()));
        }
    }

    @Override
    public ResponseResult findAllUser(FindAllUserReq findAllUserReq) {
        List<TMiningApply>list=tMiningApplyMapper.findAllUser(findAllUserReq);
        Integer count=tMiningApplyMapper.findAllUserCount(findAllUserReq);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list",list);
        jsonObject.put("count",count);
        return ResponseResult.builder().code(200).result(jsonObject).build();
    }
}
