package com.xinlian.member.server.controller;

import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.utils.CommonRedisClient;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.ListUtil;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户接收消息接口
 */
@Api(value = "用户接收消息接口")
@RestController
@RequestMapping("/{versionPath}/receiveMessage")
public class ReceiveMessageController {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private CommonRedisClient commonRedisClient;

    @ApiOperation(value = "进行redis-message测试")
    @GetMapping(value = "/redisMessageTest")
    @PassToken
    public ResponseResult redisMessageTest(){
        Long messageId = 11235L;
        String redisKey = "hashMap_123";
        List<Long> messageList = new ArrayList<>();
        messageList.add(messageId);
        //获取所有用户uid
        List<String> userIdAll = userInfoMapper.findUserIdAll();
        int num = userIdAll.size() % 100 == 0 ? userIdAll.size() / 100 : userIdAll.size() / 100 + 1;
        if(commonRedisClient.exists(redisKey)) {
            //1.拆分数组 -便于后面得提交
            List<List<String>> splitList = ListUtil.averageAssign(userIdAll, num);
            for(int i=0;i<splitList.size();i++){
                Long startTime = System.currentTimeMillis();
                List<String> splitUids = splitList.get(i);
                //uid,messageId-List
                Map<String,List<Long>> isExistsMap = new HashMap<>();
                for(String uid : splitUids) {
                    List<Long> userMessageRefList = new ArrayList<>();
                    userMessageRefList.add(messageId);
                    //存在子键值对与否
                    List<Long> messageIdList = commonRedisClient.getHash(redisKey, uid);
                    if (null != messageIdList) {
                        userMessageRefList.addAll(messageIdList);
                    }
                    //uid 新增messageId
                    isExistsMap.put(uid, userMessageRefList);
                }
                Long endTime = System.currentTimeMillis();
                //组装完后再存放 - 分批
                System.err.println("比较时间："+ (endTime-startTime));
                commonRedisClient.hashAll(redisKey,isExistsMap);
                Long endTime1 = System.currentTimeMillis();
                System.err.println("分批总执行时间："+ (endTime1-startTime));
                System.err.println("putAll时间："+ (endTime1-endTime));

            }
        }else {//如果不存在
            List<List<String>> splitList = ListUtil.averageAssign(userIdAll, num);
            for(int i=0;i<splitList.size();i++){
                Map<String,List<Long>>  hkMap= new HashMap<>();
                for(String uid:splitList.get(i)){
                    hkMap.put(uid,messageList);
                }
                commonRedisClient.hashAll(redisKey,hkMap);
            }
        }
        return ResponseResult.ok();
    }
}
