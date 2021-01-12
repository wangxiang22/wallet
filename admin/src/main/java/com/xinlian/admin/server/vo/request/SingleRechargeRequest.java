package com.xinlian.admin.server.vo.request;

import lombok.Data;

@Data
public class SingleRechargeRequest {

    public static final String params = "{uid:uid,currencyCode:币种类型,rechargeTypeName:充值类型,rechargeNumber:交易值,remark:备注}";

    //uid
    private Long uid;
    //币种类型code
    private String currencyCode;
    //充值typeName
    private String rechargeTypeName;
    //交易值
    private String rechargeNumber;
    //备注
    private String remark;
}
