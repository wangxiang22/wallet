package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum AdminReceiveMessageEnum {
    /**
     * 全部用户
     */
    ALL_USER(1,"ALL_USER"),
    /**
     * 单个用户
     */
    ONE_USER(2,"ONE_USER"),
    /**
     * 多个用户，英文逗号分隔
     */
    USER_GROUPS(3,"USER_GROUPS"),
    ;

    private Integer roleTypeCode;
    private String roleTypeName;


    AdminReceiveMessageEnum(Integer roleTypeCode,String roleTypeName){
        this.roleTypeCode=roleTypeCode;
        this.roleTypeName=roleTypeName;
    }
}
