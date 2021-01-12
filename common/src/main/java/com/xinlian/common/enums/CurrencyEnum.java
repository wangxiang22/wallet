package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum CurrencyEnum {


//    CAT(1,"CAT"),
//    CAG(2,"CAG"),
//    USDT(3,"USDT"),



    ETH(2,"ETH"),
    USDT(5,"USDT"),
    CAT(6,"CAT"),
    GPT(7,"GPT"),
    CAG(211,"CAG"),


    ;

    private int currencyId;
    private String currencyCode;

    CurrencyEnum(int currencyId ,String currencyCode){
        this.currencyId = currencyId;
        this.currencyCode = currencyCode;
    }

    public static CurrencyEnum getEnum(String currencyCode) {
        return Stream.of(CurrencyEnum.values()).filter(e -> e.getCurrencyCode().equals(currencyCode)).findFirst().orElse(null);
    }

    public static int getCurrencyIdByCurrencyCode(String currencyCode){
        try {
            currencyCode = currencyCode.toUpperCase();
            return getEnum(currencyCode).getCurrencyId();
        }catch (Exception e){
            return 0;
        }
    }

    public static CurrencyEnum getEnum(int currencyId) {
        return Stream.of(CurrencyEnum.values()).filter(e -> e.getCurrencyId()==currencyId).findFirst().orElse(null);
    }

    public static String getCurrencyCodeByCurrencyId(String currencyId){
        Long cyId = Long.parseLong(currencyId);
        return Stream.of(CurrencyEnum.values()).filter(e -> e.getCurrencyId()==cyId).findFirst().orElse(null).getCurrencyCode();
    }

    public static CurrencyEnum getEnum(Long currencyId) {
        return Stream.of(CurrencyEnum.values()).filter(e -> e.getCurrencyId()==currencyId).findFirst().orElse(null);
    }

}
