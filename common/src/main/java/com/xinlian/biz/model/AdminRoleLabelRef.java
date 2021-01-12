package com.xinlian.biz.model;

import lombok.Data;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/25 10:07
 */
@Data
public class AdminRoleLabelRef {

    private Long id;

    private Long roleId;

    private Long permId;

    private String creator;
}
