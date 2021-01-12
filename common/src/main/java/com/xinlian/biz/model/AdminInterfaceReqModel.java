package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色与接口关联
 * </p>
 *
 * @author 无名氏
 * @since 2020-08-22
 */
@Data
public class AdminInterfaceReqModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long menuId;
    //接口地址
    private String interfaceUrl;
    //接口地址描述
    private String interfaceDes;
    //状态
    private Integer status;



}
