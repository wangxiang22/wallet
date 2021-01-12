//package com.xinlian.rabbitMq;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Component;
//
//@Component("commonConfirmCallBack")
//@Slf4j
//public class CommonConfirmCallBack implements RabbitTemplate.ConfirmCallback {
//    @Override
//    public void confirm(CorrelationData correlationData, boolean b, String s) {
//        /**
//         * 当ack为false时，说明消息根据路由键没有找到交换机
//         * 需要抛出异常
//         */
//        if (!b) {
//            String errorMsg = "消息根据路由键没有找到交换机：" + s;
//            log.error("消息发送失败 msg = {}，data = {}",errorMsg, JSON.toJSONString(correlationData));
//            throw new RuntimeException(errorMsg);
//        }
//    }
//}
