///*
// * 合肥币秀科技有限公司
// * Copyright (C) 2019 All Rights Reserved.
// */
//package com.xinlian.rabbitMq;
//
//import com.rabbitmq.client.Channel;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.AcknowledgeMode;
//import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.connection.Connection;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//@Slf4j
//public class RabbitMqConfig {
//    @Autowired
//    private BaseMqConfig baseMqConfig;
//
//    private final static int BASIC_QOS = 1;
//
//    @Bean(name = "baseConnectionFactory")
//    public ConnectionFactory initConnectionFactory() {
//        final com.rabbitmq.client.ConnectionFactory rcf =
//                new com.rabbitmq.client.ConnectionFactory();
//        // 设置自动重新连接
//        rcf.setAutomaticRecoveryEnabled(true);
//        rcf.setHost(baseMqConfig.getRabbitHost());
//        rcf.setPort(baseMqConfig.getRabbitPort());
//        rcf.setUsername(baseMqConfig.getRabbitUsername());
//        rcf.setPassword(baseMqConfig.getRabbitPassword());
//        // 设置心跳时间为5秒
//        rcf.setNetworkRecoveryInterval(5000);
//        // 重新连接时不重新声明交换机和队列信息
//        rcf.setTopologyRecoveryEnabled(true);
//        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(rcf);
//        cachingConnectionFactory.setPublisherConfirms(true);
//        cachingConnectionFactory.setPublisherReturns(true);
//        return cachingConnectionFactory;
//    }
//
//    @Bean(name = "baseContainerFactory")
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
//            @Qualifier("baseConnectionFactory") final ConnectionFactory connectionFactory) {
//        return initRabbitListenerContainerFactory(connectionFactory);
//    }
//
//    @Bean(name = "baseRabbitTemplate")
//    public RabbitTemplate rabbitTemplate(
//            @Qualifier("baseConnectionFactory") final ConnectionFactory connectionFactory,
//            @Qualifier("commonConfirmCallBack") final CommonConfirmCallBack commonConfirmCallBack) {
//        return initRabbitTemplate(connectionFactory, commonConfirmCallBack);
//    }
//
//    private RabbitTemplate initRabbitTemplate(
//            ConnectionFactory connectionFactory, CommonConfirmCallBack commonConfirmCallBack) {
//        final Connection connection = connectionFactory.createConnection();
//        final Channel channel = connection.createChannel(false);
//        try {
//            // 每个消费者最多同时分配一个任务
//            channel.basicQos(BASIC_QOS);
//        } catch (final Exception e) {
//            log.error("初始化RabbitMQ队列异常：", e);
//        }
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        // 请求结果确认会回调confirm callback
//        rabbitTemplate.setConfirmCallback(commonConfirmCallBack);
//        rabbitTemplate.setMandatory(true);//开启强制委托模式
//        // 投递quene失败会执行return callback
//        rabbitTemplate.setReturnCallback((message, i, s, s1, s2) ->
//                log.error("return message = {} ,i = {} ,s ={},s1 ={},s2 ={}", message, i, s, s1, s2));
//        rabbitTemplate.setMandatory(true);
//        return rabbitTemplate;
//    }
//
//
//    private SimpleRabbitListenerContainerFactory initRabbitListenerContainerFactory(
//            ConnectionFactory connectionFactory) {
//        final SimpleRabbitListenerContainerFactory factory =
//                new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        // 设置手动ack
//        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        return factory;
//    }
//}
