package com.xinlian.admin.server.vo;

import lombok.Data;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/23 18:36
 */
@Data
public class AdminUserVo {

    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建人
     */
    private Long creater;
    /**
     * 修改时间
     */
    private String updateTime;
    /**
     * 修改人
     */
    private Long updater;

    private String accountDesc;
    /**
     * 用户状态 0禁用 1启用
     */
    private String status;

    private String statusName;

    //系统用户名对应权限组名称
    private String roleNameByUser;
}
