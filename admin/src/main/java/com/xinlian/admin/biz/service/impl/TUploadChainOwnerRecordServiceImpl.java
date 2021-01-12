package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.TUploadChainOwnerRecordService;
import com.xinlian.biz.dao.TChainOwnerMapper;
import com.xinlian.biz.dao.TUploadChainOwnerRecordMapper;
import com.xinlian.biz.model.TUploadChainOwnerRecord;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 上传链权人信息记录表 服务实现类
 * </p>
 *
 * @author 代码生成
 * @since 2020-01-14
 */
@Service
@Slf4j
public class TUploadChainOwnerRecordServiceImpl implements TUploadChainOwnerRecordService {

    @Autowired
    private TUploadChainOwnerRecordMapper uploadChainOwnerRecordMapper;
    @Autowired
    private TChainOwnerMapper chainOwnerMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public int batchInsert(List<TUploadChainOwnerRecord> list) {
        return uploadChainOwnerRecordMapper.batchInsert(list);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0/2 * * * ?")
    public void doDisposeChainOwnerData() {
        //过滤已存在的链权人
        List<TUploadChainOwnerRecord> existList = uploadChainOwnerRecordMapper.getExistChainOwners();
        if(null!=existList&&existList.size()>0){
            updateUploadChainOwnerToExist(existList);
        }
        String taskLimitRedisKey = RedisConstant.APP_REDIS_PREFIX + "TASK_LIMIT";
        Integer getLimit = redisClient.get(taskLimitRedisKey);
        List<TUploadChainOwnerRecord> list = uploadChainOwnerRecordMapper.getCoincidentChainOwner(getLimit);
        if(null==list || 0==list.size()){return;}
        updateUploadChainOwnerToDispose(list);
        List<TUploadChainOwnerRecord> successUnionList = new ArrayList<>();
        List<TUploadChainOwnerRecord> errorList = new ArrayList<>();
        list.forEach(union ->{
            if( null==union.getUserAuthSn()){
                errorList.add(union);
                return;
            }
            if(!union.getUserAuthSn().equals(union.getAuthSn())){
                errorList.add(union);
                return;
            }
            union.setDefaultCatNum(new BigDecimal("20000"));
            successUnionList.add(union);
        });
        int successResultNum = 0;
        int errorResultNum = 0;
        //修改状态
        if(null!=successUnionList&&successUnionList.size()>0){
            successResultNum = updateUploadChainOwnerToSuccess(successUnionList);
        }
        if(null!=errorList&&errorList.size()>0){
            errorResultNum = updateUploadChainOwnerToFail(errorList);
        }
        //批量插入链权人数据到对应表中
        int batchInsertResultNum = 0;
        if(null!=successUnionList&&successUnionList.size()>0){
            try {
                batchInsertResultNum = chainOwnerMapper.batchInsertChainOwner(successUnionList);
            }catch (Exception e){
                log.warn("插入链权人表数据异常:{}",e.toString(),e);
            }
        }
        if(successResultNum+errorResultNum != list.size() || batchInsertResultNum != successUnionList.size() ){
            log.warn("successResultNum+errorResultNum：{}，list.size()：{},batchInsertResultNum：{}",
                    successResultNum+errorResultNum,list.size(),batchInsertResultNum);
            int limitNewValue = list.size()/2==0 ? 1:list.size()/2;
            redisClient.set(taskLimitRedisKey,limitNewValue,120);
            throw new BizException("处理链权人数据异常!");
        }
    }

    public static void main(String[] args) {
        int limt =1;
        System.err.println(limt/2);
    }

    public int updateUploadChainOwnerToSuccess(List<TUploadChainOwnerRecord> list){
        int newStatus = 3;
        int oldStatus = 2;
        return uploadChainOwnerRecordMapper.batchUpdate(list,oldStatus,newStatus);
    }

    public int updateUploadChainOwnerToFail(List<TUploadChainOwnerRecord> list){
        int newStatus = 4;
        int oldStatus = 2;
        return uploadChainOwnerRecordMapper.batchUpdate(list,oldStatus,newStatus);
    }

    public int updateUploadChainOwnerToExist(List<TUploadChainOwnerRecord> list){
        int newStatus = 5;
        int oldStatus = 1;
        return uploadChainOwnerRecordMapper.batchUpdate(list,oldStatus,newStatus);
    }

    public int updateUploadChainOwnerToDispose(List<TUploadChainOwnerRecord> list){
        int newStatus = 2;
        int oldStatus = 1;
        return uploadChainOwnerRecordMapper.batchUpdate(list,oldStatus,newStatus);
    }
}
