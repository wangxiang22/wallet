package com.xinlian.common.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class CatWalletPayRes implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 0失败，1成功
     */
    private Integer payStatus;

    private String orderNo;
    private String payAmount;

    private Long uid;
    private Long nodeId;

    private Long catTradeOrderId;

}
