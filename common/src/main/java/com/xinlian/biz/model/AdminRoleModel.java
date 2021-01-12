package com.xinlian.biz.model;


import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminRoleModel {

    private Long id;

    private String roleCode;

    private String roleName;

    private String roleStatus;

    private String roleExplain;

    //下属管理员
    private String underlingAdmin;

    /**
     * 角色所属系统
     */
    private String systemId;


    private String userName;
    private String mobile;

}
