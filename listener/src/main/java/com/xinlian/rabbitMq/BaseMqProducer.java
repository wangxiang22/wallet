package com.xinlian.rabbitMq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class BaseMqProducer {
    @Autowired
    @Qualifier(value = "baseRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    /**
     * @param exchange      交换器
     * @param routingKey    路由键
     * @param payload       消息体
     * @param replyTo       回复消息发送的队列
     * @param correlationId UUID
     */
    public void sendMessage(String exchange, String routingKey, String payload, String replyTo, String correlationId) {
        CorrelationData correlationData = new CorrelationData(correlationId);
        MessageProperties properties = new MessageProperties();
        properties.setConsumerQueue(routingKey);
        properties.setReplyTo(replyTo);
        properties.setCorrelationId(Arrays.toString(correlationId.getBytes()));
        Message message = new Message(payload.getBytes(), properties);
        log.info("===> 发送的消息体={}, correlationId={}",payload,correlationId);
        rabbitTemplate.send(exchange, routingKey, message, correlationData);
    }
}
