package com.xinlian.common.enums;

import lombok.Getter;

/**
 * 消息内容类型
 */
@Getter
public enum AdminMessageContentEnum {
    /**
     * 账户消息
     */
    USER_MESSAGE(1,"USER_MESSAGE"),
    /**
     * 活动消息
     */
    NEWS_MESSAGE(2,"NEWS_MESSAGE"),
    /**
     * 系统消息
     */
    SYSTEM_MESSAGE(3,"SYSTEM_MESSAGE"),
    ;

    private Integer typeCode;
    private String typeName;


    AdminMessageContentEnum(Integer typeCode,String typeName){
        this.typeCode=typeCode;
        this.typeName=typeName;
    }
}
