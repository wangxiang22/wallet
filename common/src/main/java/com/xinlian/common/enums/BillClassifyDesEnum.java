package com.xinlian.common.enums;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum BillClassifyDesEnum {

    INTERNAL_TRANSFER_IN(1,7,2,"内部转账-转入"),
    INTERNAL_TRANSFER_OUT(1,7,2,"内部转账-转出"),
    CHAIN_RECHARGE(2,7,1,"链上充值"),
    WITHDRAWAL_COIN(3,7,2,"提币"),
    ROCKET_TRANSFER_IN(4,7,1,"rocket转入"),
    ROCKET_TRANSFER_OUT(4,7,2,"转出到rocket"),
    PLEDGE_MINING(5,7,2,"保证金交纳"),
    SELL_CAT_INCOME(6,7,2,"卖出cat所得"),
    BUY_CAT(6,7,2,"买入cat"),
    BUY_CAT_FEE_CAG(6,7,2,"兑入cat手续费"),
    CAT_ORDER_FREEZE(6,7,2,"cat挂单冻结"),
    ORDER_FEE_CAG_FREEZE(6,7,2,"挂单手续费-CAG冻结"),
    ORDER_TIME_OUT_RETURN(6,6,1,"挂单时间过期退还"),
    ACTIVE_MINING(7,7,3,"激活矿机"),
    GOODS_SELL(8,7,1,"出售商品"),
    GOODS_BUY(8,7,2,"购买商品"),
    ;

    /**
     * 账单分类id
     */
    private int classifyId;
    /**
     * 交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
     */
    private Integer tradeStatus;
    /**
     * 交易类型 (充值：1；提币：2;兑换：3  )
     */
    private Integer tradeType;
    /**
     * 描述
     */
    private String des;

    BillClassifyDesEnum(int classifyId,Integer tradeStatus,Integer tradeType,String des){
        this.classifyId=classifyId;
        this.tradeStatus=tradeStatus;
        this.tradeType=tradeType;
        this.des=des;
    }

    public static List<BillClassifyDesEnum> getBillClassifyDesEnumList(int classifyId) {
        return Stream.of(BillClassifyDesEnum.values()).filter(e -> e.getClassifyId() == classifyId).collect(Collectors.toList());
    }

}
