package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangJun
 * @version V1.0  2020/5/21
 **/
@Data
public class OnlineRankVO implements Serializable {
    private static final long serialVersionUID = -6655258768636133598L;
    private String uid;
    private String username;
    private Integer lastTime;
}
