package com.xinlian.member.biz.jwt.annotate;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented//说明该注解将被包含在javadoc中
public @interface EncryptionAnnotation {

    String opeType() default "";
}