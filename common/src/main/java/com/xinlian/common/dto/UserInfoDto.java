package com.xinlian.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uid;
    private Integer levelStatus;//0冻结 1普通用户 2会员
    private Integer oremState;//矿机激活状态 0未激活1已激活
    private Integer nodeId;

}

