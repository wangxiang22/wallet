package com.xinlian.member.biz.malechain.vo.request;

import lombok.Data;

@Data
public class MaleChainWithdrawCallBackRequest {

    private String tx_hash;

    private Integer status;

    private String businessId;

    private String fee;
}
