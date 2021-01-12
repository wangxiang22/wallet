package com.xinlian.member.biz.udun.aoplog;

import java.lang.annotation.*;

/**
 * 与优盾交互日志注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented//说明该注解将被包含在javadoc中
public @interface UdunLogAnnotation {

    String udunOpeType() default "";

}
