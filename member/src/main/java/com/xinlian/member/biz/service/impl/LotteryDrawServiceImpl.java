//package com.xinlian.member.biz.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.xinlian.biz.dao.TUserInfoMapper;
//import com.xinlian.biz.dao.TWalletInfoMapper;
//import com.xinlian.biz.dao.TWalletTradeOrderMapper;
//import com.xinlian.biz.model.*;
//import com.xinlian.biz.dao.LotteryDrawMapper;
//import com.baomidou.mybatisplus.service.impl.ServiceImpl;
//import com.xinlian.biz.utils.AdminOptionsUtil;
//import com.xinlian.common.dto.DrawMqTransDto;
//import com.xinlian.common.dto.LotteryDrawDto;
//import com.xinlian.common.dto.UserInfoDto;
//import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
//import com.xinlian.common.request.LotteryDrawReq;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.common.result.BizException;
//import com.xinlian.member.biz.redis.RedisClient;
//import com.xinlian.member.biz.service.LotteryDrawPrizerService;
//import com.xinlian.member.biz.service.LotteryDrawService;
//import com.xinlian.rabbitMq.BaseMqProducer;
//import com.xinlian.rabbitMq.UUIDUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static com.xinlian.common.contants.LotteryDrawConstant.*;
//import static com.xinlian.common.redis.RedisConstant.LOTTERY_DRAW_INFO;
//import static com.xinlian.rabbitMq.exchange.ExchangeConstants.GOODS_EXCHANGE;
//import static com.xinlian.rabbitMq.routingKey.RoutingKeyConstants.UPDATE_STOCK_ROUTE;
//import static com.xinlian.member.biz.redis.RedisConstant.DrawPrize;
//
///**
// * <p>
// * 服务实现类
// * </p>
// *
// * @author lx
// * @since 2020-06-05
// */
//@Service
//@Slf4j
//public class LotteryDrawServiceImpl extends ServiceImpl<LotteryDrawMapper, LotteryDraw> implements LotteryDrawService {
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private TUserInfoMapper tUserInfoMapper;
//    @Autowired
//    private LotteryDrawPrizerService lotteryDrawPrizerService;
//    @Autowired
//    private TWalletInfoMapper tWalletInfoMapper;
//    @Autowired
//    private AdminOptionsUtil adminOptionsUtil;
//    @Autowired
//    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
//    @Autowired
//    private BaseMqProducer baseMqProducer;
//
//    /**
//     * 1.点击抽奖先检查用户是否有抽奖资格
//     * 2.查看是否在抽奖时间内
//     * 3.查询是否抽过奖了
//     * 4.在奖池中随机找个奖发给用户
//     * 5.减去奖池中该奖的库存
//     *
//     * @return
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ResponseResult lotteryDraw(LotteryDrawReq lotteryDrawReq) {
//        Long uid = lotteryDrawReq.getUid();
//        //是否在抽奖时间内
//        LotteryDrawDto bootScreenRes = new LotteryDrawDto();
//        try {
//            bootScreenRes = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.LOTTERY_DRAW.getBelongsSystemCode(), LotteryDrawDto.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (System.currentTimeMillis() < Long.parseLong(bootScreenRes.getLdStartTime())||System.currentTimeMillis()>Long.parseLong(bootScreenRes.getLdEndTime())) {
//            throw new BizException(TIME_ERROR);
//        }
//        //查询是否抽过奖了
//        //测试先注了
// //        Map<Long,LotteryDrawPrizer> map = redisClient.get(LOTTERY_DRAW_INFO);
////        if (map!=null && map.get(uid.toString())!=null){
////            throw new BizException(ALREADY_GET);
////        }
//        //测试先注了
//        LotteryDrawPrizer lotteryDrawPrizer = redisClient.get(LOTTERY_DRAW_INFO.concat(uid.toString()));
//        if (lotteryDrawPrizer!=null){
//            throw new BizException(ALREADY_GET);
//        }
//        //检查用户是否有抽奖资格
//        UserInfoDto userInfoDto = redisClient.getHash("userState1", uid.toString());
//        if (userInfoDto!=null) {
//            if (userInfoDto.getOremState() != 1) {
//                throw new BizException(NO_PROMISSION);
//            }
//        }else {
//            throw new BizException(NO_PROMISSION);
//        }
//        //抽奖
//        LotteryDrawPrizer result = startLotteryDraw(lotteryDrawReq);
//        return ResponseResult.ok(result);
//    }
//
//    @Override
//    public ResponseResult findLotteryDrawState(Long uid) {
//        LotteryDrawPrizer lotteryDrawPrizer = redisClient.get(LOTTERY_DRAW_INFO.concat(uid.toString()));
//        if (lotteryDrawPrizer!=null) {
//            return ResponseResult.ok(lotteryDrawPrizer);
//        }
//        return ResponseResult.ok();
//    }
//
//
//
//    private LotteryDrawPrizer startLotteryDraw(LotteryDrawReq lotteryDrawReq) {
//        Long uid = lotteryDrawReq.getUid();
//        //获取奖项集合id正序排序
//        List<LotteryDraw> draws = redisClient.hashValues(DrawPrize);
//        List<LotteryDraw> lotteryDraws = draws.stream().sorted(Comparator.comparing(LotteryDraw::getId)).collect(Collectors.toList());
//        Integer allStock = lotteryDraws.stream().mapToInt(LotteryDraw::getStock).sum();//求库存和
//        int random = new Random().nextInt(allStock);//获取随机数
//        for (LotteryDraw lotteryDraw : lotteryDraws) {
//            //如果小于库中的num则中了该级别的奖
//            if (random <= lotteryDraw.getNum()) {
//                //中奖后判断有没有库存 如无则跳到下一段
//                if (lotteryDraw.getNum() < 1) {
//                    continue;
//                }
//                //增加中奖记录
//                LotteryDrawPrizer lotteryDrawPrizer = new LotteryDrawPrizer();
//                lotteryDrawPrizer.setPrize(lotteryDraw.getPrize());
//                lotteryDrawPrizer.setUid(uid);
//                lotteryDrawPrizer.setValue(lotteryDraw.getValue());
//                lotteryDrawPrizer.setCreateTime(new Date());
//                lotteryDrawPrizer.setUsername(lotteryDrawReq.getUsername());
//                //插入用户中奖信息
//                if (redisClient.get(LOTTERY_DRAW_INFO.concat(lotteryDrawReq.getUid().toString()))==null){
//                    redisClient.set(LOTTERY_DRAW_INFO.concat(lotteryDrawReq.getUid().toString()),lotteryDrawPrizer);
//                }
////                if (redisClient.get(LOTTERY_DRAW_INFO)==null){
////                    redisClient.set(LOTTERY_DRAW_INFO,new HashMap<>());
////                }
////                Map<Long,LotteryDrawPrizer> map = redisClient.get(LOTTERY_DRAW_INFO);
////                map.put(uid,lotteryDrawPrizer);
////                redisClient.set(LOTTERY_DRAW_INFO,map);
//                LotteryDrawPrizer result = insertUserRecordDb(lotteryDrawReq, uid, lotteryDraw);
//                return result;
//            }
//        }
//        return new LotteryDrawPrizer();
//    }
//
//    private LotteryDrawPrizer insertUserRecordDb(LotteryDrawReq lotteryDrawReq, Long uid, LotteryDraw lotteryDraw) {
//        DrawMqTransDto drawMqTransDto = new DrawMqTransDto();
//        drawMqTransDto.setLotteryDraw(lotteryDraw);
//        drawMqTransDto.setLotteryDrawReq(lotteryDrawReq);
//        drawMqTransDto.setUid(uid);
//        baseMqProducer.sendMessage("draw_redis", "draw_redis_route", JSON.toJSONString(drawMqTransDto),null , UUIDUtil.get32UpperCaseUUID());
//        //增加中奖信息
//        LotteryDrawPrizer lotteryDrawPrizer = new LotteryDrawPrizer();
//        lotteryDrawPrizer.setPrize(lotteryDraw.getPrize());
//        lotteryDrawPrizer.setUid(uid);
//        lotteryDrawPrizer.setValue(lotteryDraw.getValue());
//        lotteryDrawPrizer.setCreateTime(new Date());
//        lotteryDrawPrizer.setUsername(lotteryDrawReq.getUsername());
//        return lotteryDrawPrizer;
//    }
//}
