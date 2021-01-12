package com.xinlian.common.request;

import lombok.Data;

/**
 * com.xinlian.common.request
 *
 * @author by Song
 * @date 2020/2/23 22:37
 */
@Data
public class AdminRoleRequest {


    public static final String Params = "{roleName:角色权限中文名称,roleExplain:角色权限说明}";

    private String roleName;

    private String roleExplain;


}
