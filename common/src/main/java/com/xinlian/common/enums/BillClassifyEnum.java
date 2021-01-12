package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum BillClassifyEnum {

    INTERNAL_TRANSFER(1,"内部转账"),
    CHAIN_RECHARGE(2,"链上充值"),
    WITHDRAWAL_COIN(3,"提币"),
    ROCKET_MUTUAL_TRANSFER(4,"rocket互转"),
    PLEDGE_MINING(5,"质押挖矿"),
    SMART_CONTRACTS(6,"智能合约"),
    ACTIVE_MINING(7,"激活矿机"),
    GOODS_SELL_BUY(8,"商品出售购买"),
    ;

    /**
     * 账单分类id
     */
    private int classifyId;
    /**
     * 账单分类名称
     */
    private String billClassifyName;

    BillClassifyEnum(int classifyId,String billClassifyName){
        this.classifyId=classifyId;
        this.billClassifyName=billClassifyName;
    }

    public static BillClassifyEnum getBillClassifyEnum(String billName) {
        return Stream.of(BillClassifyEnum.values()).filter(e -> e.getBillClassifyName().equalsIgnoreCase(billName)).findFirst().orElse(null);
    }

}
