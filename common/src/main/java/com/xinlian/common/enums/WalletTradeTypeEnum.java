package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @see
 */
@Getter
public enum WalletTradeTypeEnum {


    TOP_UP(1,"链上充值"),

    /***钱包后台充值***/
    ADMIN_TOP_UP(1,"充值所得"),
    ADMIN_ACT_UP(1,"活动所得"),
    ADMIN_AIR_DROP_UP(1,"空投所得"),
    ADMIN_OTHER_TOP_UP(1,"其他所得"),


    MENTION_MONEY(2,"提币"),

    INTERNAL_TRADE_TO(2,"内部转账-转出"),

    INTERNAL_TRADE_ADD(2,"内部转账-转入"),

    PLEDGE_MINING(2,"保证金交纳"),

    //CONVERT(3,"兑换"),

    TO_ROCKET(2,"转出到rocket"),

    FROM_ROCKET(1,"rocket转入"),

    RELEASE_CAT(1,"释放CAT"),

    CAG_WALLET(1,"CAG活动奖励转入"),

    /***智能合约***/
    CAT_PENDING_ORDER(2,"CAT挂单冻结"),
    CAT_PENDING_ORDER_CAG_FEE(2,"挂单手续费-CAG冻结"),

    SELL_CAT_INCOME(2,"卖出CAT所得"),

    BUY_CAT_BUYER_SUBTRACT_USDT(2,"买入CAT"),
    BUY_CAT_BUYER_ADD_CAT(2,"买入CAT"),
    BUY_CAT_CAG_FEE(2,"兑入CAT手续费"),

    CAG_BACK(1,"挂单时间过期退还"),
    CAT_BACK(1,"挂单时间过期退还"),

    /*******矿池对GPT操作****des:矿池-转入****/
    MINING_SHIFT_TO_WALLET(1,"矿池-转入"), //CAT

    MINING_SHIFT_TO_SERVICE_CHARGE(2,"矿池-手续费"),//GPT

    BLOCKMALL_PAY(2,"布鲁克商城哥伦布钱包支付"),//布鲁克商城哥伦布钱包支付
    BUY_GOODS_USDT_AMOUNT(2,"购买商品"),//USDT
    SELL_GOODS_USDT_AMOUNT(1,"出售商品"),//USDT
    ;

    private Integer tradeType;
    private String tradeDesc;

    WalletTradeTypeEnum(Integer tradeType , String tradeDesc){
        this.tradeType = tradeType;
        this.tradeDesc = tradeDesc;
    }
//因为不是唯一
//    public static String getTradeDesc(Integer tradeType){
//        return Stream.of(WalletTradeTypeEnum.values()).filter(e -> e.getTradeType()==tradeType).findFirst().orElse(null).getTradeDesc();
//    }

    public static int getEnumTradeType(String tradeTypeDesc){
        return Stream.of(WalletTradeTypeEnum.values()).filter(e -> e.getTradeDesc()==tradeTypeDesc).findFirst().orElse(null).getTradeType();
    }
}
