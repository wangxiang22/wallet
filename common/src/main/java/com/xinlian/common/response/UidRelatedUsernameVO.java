package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangJun
 * @version V1.0  2020/5/21
 **/
@Data
public class UidRelatedUsernameVO implements Serializable {


    private static final long serialVersionUID = 9043121359796084644L;

    private String uid;
    private String username;
}
