package com.xinlian.admin.server.operationLog;

import com.xinlian.common.enums.OperationLogLevelEnum;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD, ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented//说明该注解将被包含在javadoc中
public @interface OpeAnnotation {

    /**
     * 模块名称
     * @return
     */
    OperationModuleEnum modelName() default OperationModuleEnum.LOGIN;

    /**
     * 操作类型名称
     * @return
     */
    OperationTypeEnum typeName() default OperationTypeEnum.SYSTEM_LOGIN;

    /**
     * 操作描述 前缀
     * @return
     */
    String opeDesc()  default "";

    /**
     * 记录日志等级
     * @return
     */
    OperationLogLevelEnum logLevel() default OperationLogLevelEnum.INFO;

}
