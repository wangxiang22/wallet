package com.xinlian.biz.model;


import lombok.*;

/**
 * @Description: 用户 角色 关系
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminUserRoleRef {

    private Long id;

    private Long userId;

    private Long roleId;


}
