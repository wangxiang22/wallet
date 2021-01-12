package com.xinlian.member.biz.scedule;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TPledgeMiningLogMapper;
import com.xinlian.biz.model.TPledgeMiningLog;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.dto.PledgeUidStatusDto;
import com.xinlian.common.response.PledgeMiningRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.scedule.AbstractSchedule;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.EncryptionUtil;
import com.xinlian.common.utils.HttpClientUtil;
import com.xinlian.member.biz.service.IServerNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Component
@Slf4j
public class PledgeMiningSchedule extends AbstractSchedule {
    @Autowired
    private TPledgeMiningLogMapper pledgeMiningLogMapper;
    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Value("${isTest}")
    private boolean isTest;
    //测试路径
    private static final String TEST_URL = "http://39.99.200.26:8080/mining/wallet/userApplyMining";
    //正式路径
    private static final String FORMAL_URL = "https://wk.columbu.info/mining/wallet/userApplyMining";

    /**
     * 获取cron表达式
     * @return 每隔3秒执行一次
     */
    @Override
    protected String getCron() {
        if(isTest) {
            return "0 0/25 * * * ?";
        }else{
            return "0/3 * * * * ?";
        }
    }

    /**
     * 获取质押成功的用户信息推送给算力地球
     * 1.获取符合条件但未推送的用户信息
     * 2.推送给算力地球
     * 3.status is null则新增，status=2则修改
     */
    @Override
    public void doSchedule() {
        Lock lock = redisLockRegistry.obtain("PLEDGE_LOCK_20200728");
        boolean redisLockFlag = true;
        try {
            if(!lock.tryLock()){
                log.debug(Thread.currentThread().getName()+" : 获取质押成功的用户信息推送给算力地球接口，获取分布式锁失败!");
                redisLockFlag = false;
                return;
            }
            PledgeUidStatusDto pledgeUidStatusDto = pledgeMiningLogMapper.findNotRequestOne(DateFormatUtil.getSubThreeDate(new Date()));
            if (null == pledgeUidStatusDto) {
                return;
            }
            log.info("=======定时任务：获取质押成功的用户信息推送给算力地球=======");
            //记录表中是否已有该用户的记录
            boolean isExist = false;
            TPledgeMiningLog pledgeMiningLog = new TPledgeMiningLog();
            pledgeMiningLog.setUid(pledgeUidStatusDto.getUid());
            TPledgeMiningLog tPledgeMiningLog = pledgeMiningLogMapper.selectOne(pledgeMiningLog);
            if (null != tPledgeMiningLog && tPledgeMiningLog.getStatus() == 3) {
                isExist = true;
            }
            PledgeMiningRes pledgeMiningRes = pledgeMiningLogMapper.findPledgeUserOne(pledgeUidStatusDto.getUid());
            this.replaceParentNode(pledgeMiningRes);
            Map<String,Object> map = new HashMap<>();
            //获取5位随机数
            int randNum = (int) (Math.random() * 9 + 1) * 10000;
            //获取请求参数时间戳
            long timestamp = System.currentTimeMillis();
            //获取时间戳md5加密
            String md5 = DigestUtils.md5DigestAsHex(String.valueOf(timestamp).getBytes());
            //生成请求token
            String token =randNum + md5;
            //对象转json字符串
            String pledgeMiningResJson = JSON.toJSONString(pledgeMiningRes);
            //加密数据
            try {
                String encryptString = EncryptionUtil.encryptDES(pledgeMiningResJson, EncryptionUtil.ENCRYPT_PWD);
                map.put("param",encryptString);
            } catch (Exception e) {
                log.error("质押成功用户信息加密出现异常:{}",e.toString(),e);
            }
            map.put("timestamp",timestamp);
            String url = null;
            if (isTest) {
                url = TEST_URL;
            }else {
                url = FORMAL_URL;
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("token",token);
            String post= HttpClientUtil.doJsonPost(url, map,headers);
            pledgeMiningLog.setRequestTime(new Date());
            if (StringUtils.isEmpty(post)){
                //响应结果为空，记录表status = 2，request写入字符串“响应结果为空”
                pledgeMiningLog.setStatus(2);
                pledgeMiningLog.setResult("响应结果为空");
                insertOrUpdateLog(isExist,pledgeMiningLog);
                throw new BizException("请求算力地球接口【userApplyMining】响应结果为空");
            }
            log.info("请求算力地球接口【userApplyMining】响应结果："+post);
            JSONObject jsonObject = JSON.parseObject(post);
            Integer code = jsonObject.getInteger("code");
            pledgeMiningLog.setResult(jsonObject.toJSONString());
            if (code == 200){
                //响应结果为成功，记录表status = 1，request写入响应结果
                pledgeMiningLog.setStatus(1);
                insertOrUpdateLog(isExist,pledgeMiningLog);
            }else {
                //响应结果为异常或失败，记录表status = 2，request写入响应结果
                pledgeMiningLog.setStatus(2);
                insertOrUpdateLog(isExist,pledgeMiningLog);
                throw new BizException("请求算力地球接口【userApplyMining】响应结果为异常或失败");
            }
        } catch (BizException e) {
            log.error("请求算力地球接口【userApplyMining】结果异常");
        } finally {
            if(redisLockFlag) {
                lock.unlock();
                log.debug(Thread.currentThread().getName()+" : 获取质押成功的用户信息推送给算力地球接口，释放分布式锁success");
            }
            log.debug(Thread.currentThread().getName()+" : 获取质押成功的用户信息推送给算力地球接口，释放分布式锁失败!");
        }
    }

    /**
     * 新增或修改质押成功请求算力地球接口记录表
     * @param isExist 记录表中是否已有该用户的记录
     * @param pledgeMiningLog 新增或修改的实体
     */
    private void insertOrUpdateLog(boolean isExist,TPledgeMiningLog pledgeMiningLog) {
        if (isExist) {
            //修改记录
            Integer update = pledgeMiningLogMapper.update(pledgeMiningLog, new EntityWrapper<TPledgeMiningLog>().eq("uid", pledgeMiningLog.getUid()));
            if (0 == update) {
                throw new BizException("质押成功请求算力地球接口记录表修改记录失败");
            }
        }else {
            //新增记录
            pledgeMiningLog.setCreateTime(new Date());
            Integer insert = pledgeMiningLogMapper.insert(pledgeMiningLog);
            if (0 == insert) {
                throw new BizException("质押成功请求算力地球接口记录表新增记录失败");
            }
        }
    }

    /**
     * 质押成功用户信息中节点信息替换为一级节点信息
     * @param pledgeMiningRes 查库用户信息（自己本身的节点信息）
     */
    private void replaceParentNode(PledgeMiningRes pledgeMiningRes) {
        Long nodeId = pledgeMiningRes.getNodeId().longValue();
        TServerNode node = serverNodeService.getById(nodeId);
        if (0==node.getParentId()) {
            return;
        }
        Long parentId = Long.parseLong(node.getParentIds().split(",")[1]);
        TServerNode parentNode = serverNodeService.getById(parentId);
        pledgeMiningRes.setNodeId(parentNode.getId().intValue());
        pledgeMiningRes.setNodeName(parentNode.getName());
    }
}
