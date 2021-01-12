//package com.xinlian.common.scedule;
//
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Role;
//import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
//import org.springframework.scheduling.config.TaskManagementConfigUtils;
//
///**
// * <p>
// * 定时调度配置
// * </p>
// * <pre> Created: 2020/02/18 13:36  </pre>
// *
// * @author caimingshi
// * @version 1.0
// * @since JDK 1.8
// */
//@Configuration
//public class Scheduler {
//
//    @Conditional(SchedulerCondition.class)
//    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
//        return new ScheduledAnnotationBeanPostProcessor();
//    }
//}