package com.xinlian.common.request;

import lombok.Data;

@Data
public class MiningConfigReq {
    private Long activeTime;//激活时间
    private Long nextActiveTime;//下次激活时间
    private Integer activeNum;//激活名额
}
