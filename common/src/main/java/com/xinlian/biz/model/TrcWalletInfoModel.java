package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TrcWalletInfoModel implements Serializable {

    private static final long serialVersionUID = 1L;

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



}
