package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum WalletTradeOrderStatusEnum {

    /**
     * 站内互转：1）处理中   2）已完成
     * 站外提现：1）审核中    2）【  已驳回   |   已完成  】
     */

    //交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
    APPLY(1,"申请"),
    ADMIN_PASS_PASS(2,"后台审核通过"),
    WAITING_CALLBACK(3,"等待U盾审核"),
    AUDIT_REJECT(4,"审核驳回"),
    AUDIT_PASS(5,"审核通过"),
    TRADE_FAIL(6,"交易失败"),
    TRADE_SUCCESS(7,"交易成功"),
    ;


    private Integer code;
    private String desc;

    WalletTradeOrderStatusEnum(Integer code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    public static String getEnumDesc(Integer code){
        return Stream.of(WalletTradeOrderStatusEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(null).getDesc();
    }

    /**
     * 提供给APP客户展示
     * @param code
     * @return
     */
    public static String getAppShowEnumDesc(Integer code){
        if(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode().equals(code)){
            return "已完成";
        }else if(WalletTradeOrderStatusEnum.AUDIT_REJECT.getCode().equals(code)){
            return "已驳回";
        }else if(WalletTradeOrderStatusEnum.TRADE_FAIL.getCode().equals(code)){
            return "交易失败";
        }else {
            return "审核中";
        }
    }
}
