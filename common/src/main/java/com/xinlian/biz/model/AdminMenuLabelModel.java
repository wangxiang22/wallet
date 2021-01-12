package com.xinlian.biz.model;

import lombok.Data;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/24 23:26
 */
@Data
public class AdminMenuLabelModel {

    private Long id;

    private Long menuId; // 菜单ID

    private String menuName;//菜单名称

    private String labelKey;//标签key

    private String labelName; //标签名称

    private String isChecked;//角色是否选中

    private String menuUrl;//菜单地址

    //拼接label
    private String groupConcatLab;

    //接口地址
    private String interfaceUri;
}
