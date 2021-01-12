///*
// * 合肥币秀科技有限公司
// * Copyright (C) 2019 All Rights Reserved.
// */
//package com.xinlian.member.biz.listener;
//
//import com.alibaba.fastjson.JSONObject;
//import com.rabbitmq.client.Channel;
//import com.xinlian.biz.dao.LotteryDrawMapper;
//import com.xinlian.biz.dao.LotteryDrawPrizerMapper;
//import com.xinlian.biz.dao.TWalletInfoMapper;
//import com.xinlian.biz.dao.TWalletTradeOrderMapper;
//import com.xinlian.biz.model.LotteryDraw;
//import com.xinlian.biz.model.LotteryDrawPrizer;
//import com.xinlian.biz.model.TWalletInfo;
//import com.xinlian.biz.model.TWalletTradeOrder;
//import com.xinlian.common.dto.DrawIntoDbDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//
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
//public class DrawIntoDbListener {
//
//    @Autowired
//    private LotteryDrawPrizerMapper lotteryDrawPrizerService;
//    @Autowired
//    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
//    @Autowired
//    private TWalletInfoMapper tWalletInfoMapper;
//    @Autowired
//    private LotteryDrawMapper lotteryDrawMapper;
//
//    /**
//     * 库存相关消息监听
//     */
//    @RabbitListener(queues = {"draw_db_queue"}, containerFactory = "baseContainerFactory")
//    public void process(@Payload final Object obj,
//                        @Header(AmqpHeaders.DELIVERY_TAG) final long deliveryTag,
//                        final Channel channel) throws IOException {
//
//        Message msg = (Message) obj;
//        try {
//            byte[] body = msg.getBody();
//            String message = new String(body);
//            log.info("收到库存消息 msg ={}，开始处理消息-----", message);
//            System.out.println(message);
//            DrawIntoDbDto drawIntoDbDto = JSONObject.parseObject(message, DrawIntoDbDto.class);
//            TWalletInfo twalletInfo = drawIntoDbDto.getDbResult();
//            LotteryDraw lotteryDraw = drawIntoDbDto.getLotteryDraw();
//            LotteryDrawPrizer lotteryDrawPrizer = drawIntoDbDto.getLotteryDrawPrizer();
//            TWalletTradeOrder tWalletTradeOrder = drawIntoDbDto.getTWalletTradeOrder();
//            TWalletInfo dbResult = tWalletInfoMapper.selectOne(twalletInfo);
//            dbResult.setBalanceNum(dbResult.getBalanceNum().add(new BigDecimal(lotteryDraw.getValue())));
//            lotteryDrawPrizerService.insert(lotteryDrawPrizer);
//            tWalletInfoMapper.updateById(dbResult);
//            tWalletTradeOrderMapper.insert(tWalletTradeOrder);
//            lotteryDrawMapper.subStock(lotteryDraw.getId());
//        } catch (Exception e) {
//            log.error("用户抽奖信息入库失败:{}", e);
//        }
//        channel.basicAck(deliveryTag, false);
//    }
//
//
//}
