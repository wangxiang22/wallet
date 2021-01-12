///*
// * 合肥币秀科技有限公司
// * Copyright (C) 2019 All Rights Reserved.
// */
//package com.xinlian.member.biz.listener;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.rabbitmq.client.Channel;
//import com.xinlian.biz.dao.LotteryDrawMapper;
//import com.xinlian.biz.dao.TWalletInfoMapper;
//import com.xinlian.biz.dao.TWalletTradeOrderMapper;
//import com.xinlian.biz.model.LotteryDraw;
//import com.xinlian.biz.model.LotteryDrawPrizer;
//import com.xinlian.biz.model.TWalletInfo;
//import com.xinlian.biz.model.TWalletTradeOrder;
//import com.xinlian.common.dto.DrawIntoDbDto;
//import com.xinlian.common.dto.DrawMqTransDto;
//import com.xinlian.common.request.LotteryDrawReq;
//import com.xinlian.common.result.BizException;
//import com.xinlian.member.biz.redis.RedisClient;
//import com.xinlian.member.biz.service.LotteryDrawPrizerService;
//import com.xinlian.rabbitMq.BaseMqProducer;
//import com.xinlian.rabbitMq.UUIDUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.xinlian.common.enums.CurrencyEnum.CAG;
//import static com.xinlian.common.enums.WalletTradeOrderStatusEnum.TRADE_SUCCESS;
//import static com.xinlian.common.enums.WalletTradeTypeEnum.CAG_WALLET;
//import static com.xinlian.common.redis.RedisConstant.LOTTERY_DRAW_INFO;
//import static com.xinlian.member.biz.redis.RedisConstant.DrawPrize;
//import static com.xinlian.rabbitMq.exchange.ExchangeConstants.GOODS_EXCHANGE;
//import static com.xinlian.rabbitMq.queue.QueueConstants.UPDATE_STOCK_QUEUE;
//import static com.xinlian.rabbitMq.routingKey.RoutingKeyConstants.UPDATE_STOCK_DB;
//import static com.xinlian.rabbitMq.routingKey.RoutingKeyConstants.UPDATE_STOCK_ROUTE;
//
///**
// * <p>
// * 库存处理监听
// * </p>
// *
// * @author cms
// * @since 2020-04-14
// */
//@Slf4j
//@Component
//public class PointsOperationListener {
//
//    @Autowired
//    private LotteryDrawPrizerService lotteryDrawPrizerService;
//    @Autowired
//    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
//    @Autowired
//    private TWalletInfoMapper tWalletInfoMapper;
//    @Autowired
//    private LotteryDrawMapper lotteryDrawMapper;
//    @Autowired
//    private RedisClient redisClient;
//    @Autowired
//    private BaseMqProducer baseMqProducer;
//
//    /**
//     * 库存相关消息监听
//     */
//    @RabbitListener(queues = {UPDATE_STOCK_QUEUE}, containerFactory = "baseContainerFactory")
//    public void process(@Payload final Object obj,
//                        @Header(AmqpHeaders.DELIVERY_TAG) final long deliveryTag,
//                        final Channel channel) throws IOException {
//
//        Message msg = (Message) obj;
//        try {
//            byte[] body = msg.getBody();
//            String message = new String(body);
//            log.info("收到库存消息 msg ={}，开始处理消息-----", message);
//            DrawMqTransDto drawMqTransDto = JSONObject.parseObject(message, DrawMqTransDto.class);
//            LotteryDraw lotteryDraw = drawMqTransDto.getLotteryDraw();
//            Long uid = drawMqTransDto.getUid();
//            LotteryDrawReq lotteryDrawReq = drawMqTransDto.getLotteryDrawReq();
//            //如有库存 则该用户成功中奖 减库存，增加中奖记录
//            lotteryDraw.setStock(lotteryDraw.getStock() - 1);
//            //减库存
//            LotteryDraw redisLotteryDraw = redisClient.getHash(DrawPrize, lotteryDraw.getCode());
//            if (redisLotteryDraw.getStock()<1){
//                log.error(redisLotteryDraw.getCode()+"等奖已无库存抽奖失败");
//                throw new BizException(redisLotteryDraw.getCode()+"等奖已无库存抽奖失败");
//            }
//            redisLotteryDraw.setStock(redisLotteryDraw.getStock()-1);
//            redisClient.hash(DrawPrize,lotteryDraw.getCode(),redisLotteryDraw);
//            //增加中奖信息
//            LotteryDrawPrizer lotteryDrawPrizer = new LotteryDrawPrizer();
//            lotteryDrawPrizer.setPrize(lotteryDraw.getPrize());
//            lotteryDrawPrizer.setUid(uid);
//            lotteryDrawPrizer.setValue(lotteryDraw.getValue());
//            lotteryDrawPrizer.setCreateTime(new Date());
//            lotteryDrawPrizer.setUsername(lotteryDrawReq.getUsername());
//            //插入用户中奖信息
//            if (redisClient.get(LOTTERY_DRAW_INFO)==null){
//                redisClient.set(LOTTERY_DRAW_INFO,new HashMap<>());
//            }
//            Map<Long,LotteryDrawPrizer> map = redisClient.get(LOTTERY_DRAW_INFO);
//            map.put(uid,lotteryDrawPrizer);
//            redisClient.set(LOTTERY_DRAW_INFO,map);
//            //增加用户钱包金额
//            TWalletInfo tWalletInfo = new TWalletInfo();
//            tWalletInfo.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));//cag
//            tWalletInfo.setUid(uid);
//            //增加交易记录
//            TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
//            tWalletTradeOrder.setTradeCurrencyNum(new BigDecimal(lotteryDraw.getValue()));
//            tWalletTradeOrder.setCurrencyId(Long.valueOf(CAG.getCurrencyId()));
//            tWalletTradeOrder.setCurrencyCode(CAG.getCurrencyCode());
//            tWalletTradeOrder.setDes(CAG_WALLET.getTradeDesc());
//            tWalletTradeOrder.setCreateTime(new Date());
//            tWalletTradeOrder.setTradeStatus(TRADE_SUCCESS.getCode());
//            tWalletTradeOrder.setUid(uid);
//            tWalletTradeOrder.setTradeType(CAG_WALLET.getTradeType());
//            //发送消息修改数据库
//            DrawIntoDbDto drawIntoDbDto = new DrawIntoDbDto();
//            drawIntoDbDto.setDbResult(tWalletInfo);
//            drawIntoDbDto.setLotteryDraw(lotteryDraw);
//            drawIntoDbDto.setLotteryDrawPrizer(lotteryDrawPrizer);
//            drawIntoDbDto.setTWalletTradeOrder(tWalletTradeOrder);
//            baseMqProducer.sendMessage(GOODS_EXCHANGE, UPDATE_STOCK_DB, JSON.toJSONString(drawIntoDbDto),null , UUIDUtil.get32UpperCaseUUID());
//        } catch (Exception e) {
//            log.error("库存消息处理异常：{}", e);
//        }
//        channel.basicAck(deliveryTag, false);
//    }
//
//
//}
