package com.xinlian.biz.model;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/21 17:16
 */

import lombok.Data;

@Data
public class AdminRoleMenuRef {

    private Long id;

    private Long roleId;

    private Long menuId;

    private String creator;
}
