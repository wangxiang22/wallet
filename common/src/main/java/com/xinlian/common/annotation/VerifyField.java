package com.xinlian.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerifyField {

    String regexValue() default "";

    //字段是否必填
    boolean fileValueRequired() default true;
}

