package com.xinlian.member.biz.udun.vo.request;

import lombok.Data;

/**
 * @author Song
 * @date 2020-05-16 11:00
 * @description
 */
@Data
public class MaleChainSearchResultRequest {

    //充值:recharge, 提现:extract
    private String type;
    private String tx_hash;
}
