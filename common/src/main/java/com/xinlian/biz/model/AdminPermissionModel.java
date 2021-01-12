package com.xinlian.biz.model;

import lombok.Data;

/**
 * 菜单标签 JavaBean
 */
@Data
public class AdminPermissionModel {

    private Long id;  //主键ID

    private Long menuId; //菜单ID

    private String menuName; //菜单名称

    private String labelKey; //标签key

    private String labelName;

    private String interfaceUri;
    //角色名称
    private String roleName;
    //角色下属操作人描述
    private String underlingOpeUserName;
    //权限描述
    private String authorityDesc;

}

