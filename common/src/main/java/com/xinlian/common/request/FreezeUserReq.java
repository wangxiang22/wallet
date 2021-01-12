package com.xinlian.common.request;

import lombok.Data;

@Data
public class FreezeUserReq {
    //会员 id
    private Long id;
    private Long uid;
    //冻结原因
    private String freezeReson;
    //客户能看到的冻结原因
    private String showFreezeReason;

}
