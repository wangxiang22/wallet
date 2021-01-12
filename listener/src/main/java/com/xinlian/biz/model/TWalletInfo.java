package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TWalletInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 客户id
     */
    private Long uid;
    /**
     * 币种id
     */
    private Long currencyId;
    /**
     * 币种编码
     */
    private String currencyCode;
    /**
     * 币种地址-冗余-取掉关联关系
     */
    private String currencyAddress;

    /**
     * U盾提供的币种地址
     */
    private String udunCurrencyAddress;
    /**
     * 服务节点id
     */
    private Long serverNodeId;
    /**
     * 余额-币种数量
     */
    private BigDecimal balanceNum;
    /**
     * 冻结币种数量
     */
    private BigDecimal frozenNum;

    @Override
    public String toString() {
        return "TWalletInfo{" +
        ", id=" + id +
        ", uid=" + uid +
        ", currencyId=" + currencyId +
        ", currencyCode=" + currencyCode +
        ", currencyAddress=" + currencyAddress +
        ", serverNodeId=" + serverNodeId +
        ", balanceNum=" + balanceNum +
        ", frozenNum=" + frozenNum +
        "}";
    }

}
