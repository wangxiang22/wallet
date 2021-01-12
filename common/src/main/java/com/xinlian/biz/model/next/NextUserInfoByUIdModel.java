package com.xinlian.biz.model.next;

import lombok.Data;

import java.util.Date;

/**
 * @author lt
 * @date 2020/09/17
 **/
@Data
public class NextUserInfoByUIdModel {

    private Long uid;

    private String realName;

    private String authSn;

    private String mobile;

    private Integer oremState;

    private Date activeTime;

    private int currentLevel;

    private Long uParentUid;

    private Long parentUid;

    private String parentAuthSn;

    private String parentRealName;

}
