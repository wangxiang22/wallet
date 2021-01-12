package com.xinlian.common.request;

import lombok.Data;


/**
 * @author lt
 */
@Data
public class NodeCurrencyReq {
    /**
     * 节点id
     */
    private Long nodeId;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 币种id
     */
    private Integer coinId;
}
