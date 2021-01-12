package com.xinlian.member.biz.udun.vo.request;

import lombok.Data;

/**
 * maleChain 提币请求参数
 */
@Data
public class MaleChainExtractRequest {

    private String to;
    private String from;
    private Long value;
    /**
     * 业务id
     */
    private Long businessId;
}
