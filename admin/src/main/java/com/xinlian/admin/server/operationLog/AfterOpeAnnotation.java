package com.xinlian.admin.server.operationLog;

import com.xinlian.common.enums.OperationLogLevelEnum;
import com.xinlian.common.enums.OperationModuleEnum;

import java.lang.annotation.*;

/**
 * com.xinlian.admin.server.operationLog
 *
 * @author by Song
 * @date 2020/2/19 15:29
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AfterOpeAnnotation {

    /**
     * 模块名称
     *
     * @return
     */
    OperationModuleEnum modelName() default OperationModuleEnum.LOGIN;

    /**
     * 类型名称
     *
     * @return
     */
    String typeName() default "";

    OperationLogLevelEnum logLevel() default OperationLogLevelEnum.INFO;
}
