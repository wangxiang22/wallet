package com.xinlian.member.biz.trcusdt.vo.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TrcUsdtRechargeRequest {

    //地址
    private String address;
    //转账数量
    private BigDecimal value;
    //交易hash
    private String txid;
    //矿工费
    private String fee;
}
