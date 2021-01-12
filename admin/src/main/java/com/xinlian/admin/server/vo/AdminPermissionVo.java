package com.xinlian.admin.server.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/25 22:01
 */
@Data
public class AdminPermissionVo {

    @ApiModelProperty(value = "主键ID,用于修改，删除等操作")
    private Long id;  //主键ID
    @ApiModelProperty(value = "角色名称")
    private String roleName; //角色名称
    @ApiModelProperty(value = "角色下属操作人描述")
    //角色下属操作人描述
    private String underlingOpeUserName;
    //权限描述
    @ApiModelProperty(value = "权限描述")
    private String authorityDesc;
}
