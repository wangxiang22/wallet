package com.xinlian.common.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CatWalletPayStatusReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 布鲁克商城订单号
     */
    private String orderNo;
}
