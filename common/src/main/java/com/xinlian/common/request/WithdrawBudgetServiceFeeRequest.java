package com.xinlian.common.request;

import lombok.Data;

@Data
public class WithdrawBudgetServiceFeeRequest {

    //币id
    private String coin_id;
    //提币地址
    private String address;
    //地址id
    private String address_id;
    //提币数量
    private String num;
    //客户id
    private Long userId;
    //客户所在节点id
    private Long serverNodeId;

    public static final String PARAMS = "{userId:客户id,coin_id:币id,address:提币地址,num:提币数量,serverNode:客户所在节点id}";
}
