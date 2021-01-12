package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangJun
 * @version V1.0  2020/5/21
 **/
@Data
public class UserTotalDurationVo implements Serializable {
    private static final long serialVersionUID = 2232217875748463658L;
    private String uid;
    private Integer totalDuration;
}
