package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * com.xinlian.common.enums
 *
 * @author by Song
 * @date 2020/2/23 18:45
 */
@Getter
public enum AdminUserStatusEnum {

    FORBIDDEN(0,"禁用"),
    START_USING(1,"启用"),
    DELETE(2,"删除"),
    ;

    private Integer code;
    private String desc;

    AdminUserStatusEnum(int code , String desc){
        this.code=code;
        this.desc=desc;
    }

    public static String getEnumDesc(Integer code){
        return Stream.of(AdminUserStatusEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null).getDesc();
    }
}
