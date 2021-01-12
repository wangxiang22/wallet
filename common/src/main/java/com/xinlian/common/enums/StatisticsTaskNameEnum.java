package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum StatisticsTaskNameEnum {




    STATIC_WALLET_INFO("STATIC_WALLET_INFO","STATIC_WALLET_INFO"),
    STATIC_OFF_SITE_RECHARGE_NUM("STATIC_OFF_SITE_RECHARGE_NUM","统计订单-链上充值"),
    STATIC_OFF_SITE_WITHDRAW("STATIC_OFF_SITE_WITHDRAW","统计订单-站外提现"),
    STATIC_FROM_ROCKET_NUM("STATIC_FROM_ROCKET_NUM","统计订单-火箭到钱包"),
    STATIC_TO_ROCKET_NUM("STATIC_TO_ROCKET_NUM","统计订单-钱包到火箭"),
    STATIC_OTHER_RECORDED("STATIC_OTHER_RECORDED","统计订单-其他入账"),
    STATIC_OTHER_CHARGE_OFF("STATIC_OTHER_CHARGE_OFF","统计订单-其他出账"),


    SUMMARY_TOP_NODE_DATA("SUMMARY_TOP_NODE_DATA","统计订单-汇总顶级节点数据"),
    ;

    private String code;
    private String desc;

    StatisticsTaskNameEnum(String code , String desc){
        this.code = code;
        this.desc = desc;
    }



}
