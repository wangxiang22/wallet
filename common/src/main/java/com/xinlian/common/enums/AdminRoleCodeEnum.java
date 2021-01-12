package com.xinlian.common.enums;

import lombok.Getter;

/**
 * com.xinlian.common.enums
 *
 * @author by Song
 * @date 2020/2/24 11:40
 */
@Getter
public enum AdminRoleCodeEnum {

    ADMIN("administrator","管理员"),
    UNDER_VIEW("UNDER_VIEW","审核中"),
    SUCCESS("SUCCESS","交易成功");

    private String roleCode;
    private String roleName;

    AdminRoleCodeEnum(String roleCode, String roleName){
        this.roleCode=roleCode;
        this.roleName=roleName;
    }
}
