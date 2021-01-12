package com.xinlian.common.request;

import lombok.Data;

import java.util.Date;

@Data
public class OrderSendReq {

    /**
     * 訂單id
     */
    private Long orderId;

    /**
     * 物流公司名稱
     */
    private String expressCompanyName;

    /**
     * 物流公司代碼
     */
    private String expressCompanyComId;

    /**
     * 物流公司單號
     */
    private String expressCode;

}
