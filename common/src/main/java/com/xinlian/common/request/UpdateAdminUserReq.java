package com.xinlian.common.request;

import lombok.Data;

@Data
public class UpdateAdminUserReq {
    private Long loginUserId;
    private String username;
    private String realName;
    //账户描述
    private String accountDesc;
    private String password;
    private Long roleId;
    private String roleCode;
    private Long updateAdminUserId;

    public static final String Params =
            "{userName:注册账号,password:密码,roleId:角色id,roleCode:角色code," +
            "accountDesc:账户描述,updateAdminUserId:修改系统用户主键id}";

}
