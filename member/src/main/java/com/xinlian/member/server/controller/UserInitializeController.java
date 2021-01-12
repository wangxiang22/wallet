//package com.xinlian.member.server.controller;
//
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import com.xinlian.biz.dao.TUserInfoMapper;
//import com.xinlian.biz.model.LotteryDraw;
//import com.xinlian.biz.model.LotteryDrawPrizer;
//import com.xinlian.common.dto.UserInfoDto;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.utils.ListUtil;
//import com.xinlian.member.biz.jwt.annotate.PassToken;
//import com.xinlian.member.biz.redis.RedisClient;
//import com.xinlian.member.biz.service.LotteryDrawPrizerService;
//import com.xinlian.member.biz.service.LotteryDrawService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import static com.xinlian.common.contants.LotteryDrawConstant.ALREADY_GET;
//import static com.xinlian.common.redis.RedisConstant.LOTTERY_DRAW_INFO;
//import static com.xinlian.member.biz.redis.RedisConstant.DrawPrize;
//
//@RestController
//@RequestMapping("userInitialize")
//@Api("初始化用户信息")
//@Slf4j
//public class UserInitializeController{
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private TUserInfoMapper tUserInfoMapper;
//    @Autowired
//    private RedisTemplate redisTemplate;
//    @Autowired
//    private LotteryDrawService lotteryDrawService;
//    @Autowired
//    private LotteryDrawPrizerService lotteryDrawPrizerService;
//
////    @ApiOperation("初始化")
////    @GetMapping("init")
////    @PassToken
////    public ResponseResult getUserInfoMap(@RequestParam String auth){
////        if(!"authInitlx0909".equals(auth))return ResponseResult.error();
////        List<UserInfoDto> list = tUserInfoMapper.queryUserState();
////        Map<Long, UserInfoDto> longUserInfoDtoImmutableMap = Maps.uniqueIndex(list, UserInfoDto::getUid);
////        Map<byte[], byte[]>  map = new HashMap<>();
////        Iterator<Map.Entry<Long, UserInfoDto>> entries = longUserInfoDtoImmutableMap.entrySet().iterator();
////        RedisConnection connection=null;
////        try{
////            connection = redisTemplate.getConnectionFactory().getConnection();
////            while(entries.hasNext()){
////                Map.Entry<Long, UserInfoDto> entry = entries.next();
////                byte[] keys = entry.getKey().toString().getBytes();
////                byte[] value = entry.getValue().toString().getBytes();
////                connection.hSet("test".getBytes(),keys,value);
////                map.put(keys,value);
////            }
////            log.info("用户激活信息redis存储success");
////            return ResponseResult.ok();
////        }catch (Exception e){
////            log.error("用户激活信息redis存储失败",e);
////            return ResponseResult.error();
////        }finally {
////            if(connection!=null) connection.close();
////        }
////    }
//    @ApiOperation("初始化")
//    @GetMapping("init")
//    @PassToken
//    public ResponseResult getUserInfoMap(@RequestParam String auth){
//        Long startTime =  System.currentTimeMillis();
//        if(!"authInitlx0909".equals(auth))return ResponseResult.error();
//        try{
//            List<UserInfoDto> list = tUserInfoMapper.queryUserState();
//            int num = list.size() % 100000 == 0 ? list.size() / 100000 : list.size() / 100000 + 1;
//            List<List<UserInfoDto>>  splitsList = ListUtil.averageAssign(list, num);
//            for(int i = 0 ;i<splitsList.size();i++){
//                Map<String, UserInfoDto> longUserInfoDtoImmutableMap = Maps.uniqueIndex(splitsList.get(i), UserInfoDto::getUid);
//                redisClient.hashAll("userState1",longUserInfoDtoImmutableMap);
//            }
//            log.info("耗时:{}",System.currentTimeMillis() - startTime);
//            return ResponseResult.ok();
//        }catch (Exception e){
//            log.error("用户激活信息redis存储失败",e);
//            return ResponseResult.error();
//        }
//    }
//
//    @ApiOperation("初始化抽奖信息")
//    @GetMapping("initDrawPrize")
//    @PassToken
//    public ResponseResult initDrawPrize(@RequestParam String auth){
//        if(!"authInitlx0909".equals(auth))return ResponseResult.error();
//        List<LotteryDraw> list = lotteryDrawService.selectList(new EntityWrapper<LotteryDraw>().orderBy("id", true));
////        redisClient.set(DrawPrize, JSON.toJSONString(list));
//        ImmutableMap<String, LotteryDraw> stringLotteryDrawImmutableMap = Maps.uniqueIndex(list, LotteryDraw::getCode);
//        redisClient.hashAll(DrawPrize, stringLotteryDrawImmutableMap);
//        return ResponseResult.ok();
//    }
//
//    @ApiOperation("初始化用户中奖信息")
//    @GetMapping("getUserDrawInfo")
//    @PassToken
//    public ResponseResult getUserDrawInfo(@RequestParam String auth){
//        if(!"authInitlx0909".equals(auth))return ResponseResult.error();
//
//        try {
//            Map<String, LotteryDrawPrizer> map = redisClient.get(LOTTERY_DRAW_INFO);
//            for (Map.Entry<String, LotteryDrawPrizer> entry : map.entrySet()) {
//                redisClient.set(entry.getKey(), entry.getValue());
//            }
//        }catch (Exception e){
//            throw new BizException("初始化用户中奖信息异常");
//        }
//        return ResponseResult.ok();
//    }
//
//    @ApiOperation("同步")
//    @GetMapping("tongbu")
//    @PassToken
//    public ResponseResult tongbu(@RequestParam String auth){
//        if(!"authInitlx0909".equals(auth))return ResponseResult.error();
//        List<LotteryDrawPrizer> lotteryDrawPrizers = lotteryDrawPrizerService.selectList(new EntityWrapper<LotteryDrawPrizer>());
//        for (LotteryDrawPrizer lotteryDrawPrizer : lotteryDrawPrizers) {
//            redisClient.set(LOTTERY_DRAW_INFO.concat(lotteryDrawPrizer.getUid().toString()),lotteryDrawPrizer);
//        }
//        return ResponseResult.ok();
//    }
//}
