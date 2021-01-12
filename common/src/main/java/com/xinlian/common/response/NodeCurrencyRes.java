package com.xinlian.common.response;

import lombok.Data;

/**
 * @author lt
 */
@Data
public class NodeCurrencyRes {
    /**
     * 是否可以充值 - 0：不可以充值，1：可以充值
     */
    private Integer rechargeStatus;
    /**
     * 是否可以提现 - 0：不可以提现，1：可以提现
     */
    private Integer cashStatus;
}
