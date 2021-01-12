//package com.xinlian.member.biz.bean;
//
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import com.xinlian.biz.dao.TUserInfoMapper;
//import com.xinlian.biz.model.TUserInfo;
//import com.xinlian.common.dto.UserInfoDto;
//import com.xinlian.member.biz.redis.RedisClient;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Component
//@Data
//@Slf4j
//public class UserBean {
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private TUserInfoMapper tUserInfoMapper;
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    /**
//     * 所有激活用户信息初始化
//     */
//    @Bean
//    public void getUserInfoMap(){
//        List<UserInfoDto> list = tUserInfoMapper.queryUserState();
//        Map<Long, UserInfoDto> longUserInfoDtoImmutableMap = Maps.uniqueIndex(list, UserInfoDto::getUid);
//        Map<byte[], byte[]>  map = new HashMap<>();
//        Iterator<Map.Entry<Long, UserInfoDto>> entries = longUserInfoDtoImmutableMap.entrySet().iterator();
//        RedisConnection connection=null;
//        try{
//            connection = redisTemplate.getConnectionFactory().getConnection();
//        while(entries.hasNext()){
//            Map.Entry<Long, UserInfoDto> entry = entries.next();
//            byte[] keys = entry.getKey().toString().getBytes();
//            byte[] value = entry.getValue().toString().getBytes();
//            connection.hSet("test".getBytes(),keys,value);
//            map.put(keys,value);
//        }
//        }catch (Exception e){
//            log.error("redis operate fail",e);
//        }finally {
//            if(connection!=null) connection.close();
//        }
//
////        while(entries.hasNext()){
////            Map.Entry<Long, UserInfoDto> entry = entries.next();
////            Long key = entry.getKey();
////            UserInfoDto value = entry.getValue();
////            redisClient.hash("userState",key.toString(),value);
////            System.out.println(longUserInfoDtoImmutableMap.get(key));
////        }
//
//        log.info("所有用户状态更新成功");
//    }
//
//}
