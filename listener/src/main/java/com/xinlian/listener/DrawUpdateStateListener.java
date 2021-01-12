/*
 * 合肥币秀科技有限公司
 * Copyright (C) 2019 All Rights Reserved.
 */
package com.xinlian.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinlian.biz.model.*;
import com.xinlian.rabbitMq.BaseMqProducer;
import com.xinlian.rabbitMq.UUIDUtil;
import com.xinlian.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import static com.xinlian.redis.RedisConstant.DrawPrize;


/**
 * <p>
 * 库存处理监听
 * </p>
 *
 * @author cms
 * @since 2020-04-14
 */
@Slf4j
@Component
public class DrawUpdateStateListener {
    @Autowired
    private BaseMqProducer baseMqProducer;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedisLockRegistry redisLockRegistry;

    /**
     * 库存相关消息监听
     */
    @RabbitListener(queues = {"draw_redis_queue"}, containerFactory = "baseContainerFactory")
    public void process(@Payload final Object obj,
                        @Header(AmqpHeaders.DELIVERY_TAG) final long deliveryTag,
                        final Channel channel) throws IOException {
        Message msg = (Message) obj;
        byte[] body = msg.getBody();
        String message = new String(body);
        log.info("收到库存消息 msg ={}，开始处理消息-----", message);
        DrawMqTransDto drawMqTransDto = JSONObject.parseObject(message, DrawMqTransDto.class);
        LotteryDraw lotteryDraw = drawMqTransDto.getLotteryDraw();
        Long uid = drawMqTransDto.getUid();
        String redisLockKey = new StringBuffer("doSubMessage"+uid).toString();
        Lock lock = redisLockRegistry.obtain(redisLockKey);
        boolean redisLockFlag = true;
        try {
            if(!lock.tryLock()){
                log.debug(Thread.currentThread().getName()+" : 库存相关消息监听，获取分布式锁失败!lockKey:{}",redisLockKey);
                redisLockFlag = false;
                return;
            }
            LotteryDrawReq lotteryDrawReq = drawMqTransDto.getLotteryDrawReq();
            //如有库存 则该用户成功中奖 减库存，增加中奖记录
            lotteryDraw.setStock(lotteryDraw.getStock() - 1);
            //减库存
            LotteryDraw redisLotteryDraw = redisClient.getHash(DrawPrize, lotteryDraw.getCode());
            if (redisLotteryDraw.getStock() < 1) {
                log.error(redisLotteryDraw.getCode() + "等奖已无库存抽奖失败");
            }
            redisLotteryDraw.setStock(redisLotteryDraw.getStock() - 1);
            redisClient.hash(DrawPrize, lotteryDraw.getCode(), redisLotteryDraw);
            //增加中奖信息
            LotteryDrawPrizer lotteryDrawPrizer = new LotteryDrawPrizer();
            lotteryDrawPrizer.setPrize(lotteryDraw.getPrize());
            lotteryDrawPrizer.setUid(uid);
            lotteryDrawPrizer.setValue(lotteryDraw.getValue());
            lotteryDrawPrizer.setCreateTime(new Date());
            lotteryDrawPrizer.setUsername(lotteryDrawReq.getUsername());
            //插入用户中奖信息
//            if (redisClient.get(LOTTERY_DRAW_INFO) == null) {
//                redisClient.set(LOTTERY_DRAW_INFO, new HashMap<>());
//            }
//            Map<Long, LotteryDrawPrizer> map = redisClient.get(LOTTERY_DRAW_INFO);
//            map.put(uid, lotteryDrawPrizer);
//            redisClient.set(LOTTERY_DRAW_INFO, map);
            //增加用户钱包金额
            TWalletInfo tWalletInfo = new TWalletInfo();
            tWalletInfo.setCurrencyId(Long.valueOf(211));//cag
            tWalletInfo.setUid(uid);
            //增加交易记录
            TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
            tWalletTradeOrder.setTradeCurrencyNum(new BigDecimal(lotteryDraw.getValue()));
            tWalletTradeOrder.setCurrencyId(Long.valueOf(211));
            tWalletTradeOrder.setCurrencyCode("CAG");
            tWalletTradeOrder.setDes("CAG活动奖励转入");
            tWalletTradeOrder.setCreateTime(new Date());
            tWalletTradeOrder.setTradeStatus(7);
            tWalletTradeOrder.setUid(uid);
            tWalletTradeOrder.setTradeType(1);
            //发送消息修改数据库
            DrawIntoDbDto drawIntoDbDto = new DrawIntoDbDto();
            drawIntoDbDto.setDbResult(tWalletInfo);
            drawIntoDbDto.setLotteryDraw(lotteryDraw);
            drawIntoDbDto.setLotteryDrawPrizer(lotteryDrawPrizer);
            drawIntoDbDto.setTWalletTradeOrder(tWalletTradeOrder);
            baseMqProducer.sendMessage("draw_db", "draw_db_route", JSON.toJSONString(drawIntoDbDto), null, UUIDUtil.get32UpperCaseUUID());
        } catch (Exception e) {
            log.error("用户抽奖信息入库失败:{}", e);
        }finally {
            if(redisLockFlag){
                lock.unlock();
            }
        }
        channel.basicAck(deliveryTag, false);
    }


}
