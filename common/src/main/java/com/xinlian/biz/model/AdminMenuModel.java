package com.xinlian.biz.model;

import lombok.*;

import java.util.List;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/21 21:33
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdminMenuModel {

    private Long id;

    private String menuName;//菜单名称

    private String menuCode;//菜单code

    private Integer menuLevel; //菜单级别

    private String menuUrl;//菜单地址

    private String menuExplain;//菜单说明

    private Long parentMenuId;//菜单所属父菜单ID

    private String systemId;//菜单所属系统

    private String status;//菜单状态

    private List<AdminMenuModel> childMenus; //子菜单

    private Boolean isChecked;//是否选中 true：选中  false:不选中
}
