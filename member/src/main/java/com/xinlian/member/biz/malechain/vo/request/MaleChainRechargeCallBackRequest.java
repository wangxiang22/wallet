package com.xinlian.member.biz.malechain.vo.request;

import lombok.Data;

@Data
public class MaleChainRechargeCallBackRequest {

    //地址
    private String address;
    //转账数量
    private Long tradeNumber;
    //交易hash
    private String tx_hash;
    //矿工费
    private String fee;
}
