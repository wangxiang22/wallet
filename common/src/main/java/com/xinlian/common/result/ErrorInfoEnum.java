package com.xinlian.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorInfoEnum implements ErrorInterface {


    AFRESH_LOGIN_CODE(401,"登录凭证已失效，请重新登录!"),
    PARAM_ERR(999,"参数错误"),
    FAILED(201, "失败"),
    SUCCESS(200, "成功"),

    NEW_VERSION(2000,"存在新版本"),

    SHOW_FREEZE_REASON(202084,"展示客户冻结具体原因"),

    LOGIN_ERROR(100013,"手机号或者密码错误！"),
    LOGIN_NULL_ERROR(100013,"手机号码或者密码不能为空！"),
    USER_IDCARD_ERROR(100013,"身份证后6位输入错误"),
    USER_TRADE_CHECK_ERROR_TIMES_MAX(100014,"今日错误次数已达上限"),
    REGISTER_MOBILE_AMOUNT_OUT(100015,"当前手机号已超过注册数量限制！"),


    WALLET_INFO_TRADSFER_ACCOUNTS_CHECK(600000,"不可以进行操作，请联系系统管理员!"),

    WALLET_INFO_TRADE_ORDER_1(600000,"节点下不能进行提现!"),
    WALLET_INFO_TRADE_ORDER_2(600000,"节点下某币种不能进行转账!"),
    WALLET_INFO_TRADE_ORDER_3(600000,"币种下不能进行提现!"),
    WALLET_INFO_TRADE_ORDER_4(600000,"节点下某币种不能进行充值!"),
    WALLET_INFO_TRADE_ORDER_5(600000,"节点下某币种不能进行提现!"),
    WALLET_INFO_TRADE_ERROR_TURN_YOURSELF(600000,"不能转给自己"),
    /***udun接口回调***/
    UDUN_CALLBACK_ERROR(700000,"签名校验错误"),
    CALLBACK_PARSE_JSON_ERROR(700001,"解析json异常"),

    EXIST_CHARGE_MONEY_TRADE(700002, "充币回调出现重复数据!"),

    DEPOSIT_AMOUNT_ERROR(700003, "充币数量错误!"),
    DEPOSIT_AMOUNT_INSERT_ERROR(700003, "充币插入数据库出现错误!"),

    CALL_BACK_WALLET_INFO_ERROR(700004, "未获取到对应钱包信息!"),


    CURRENCY_CODE_OPEN_WITHDRAW(700005,"等待该币种开通提现！"),
    CURRENCY_CODE_OPEN_RECHARGE(700005,"等待该币种开发充值！"),
    CURRENCY_INTERNAL_TRANSFER (700005,"等待该币种开通内部转账！"),
    NOT_SUFFICIENT_FUNDS (700005,"您的提现余额不足！"),
    WITH_DRAW_LOCKED_POSITION  (700005,"您有100CAG锁仓中！"),
    CURRENCY_NUM_LESS_TRADE_FEE (700005,"提币数值小于转账费用！"),
    SERVER_NODE_NOT_TRANSFERS_BETWEEN(700005,"该节点暂未开通内部互转！"),
    SERVER_NODE_NOT_RECHARGE(700005,"对方节点暂未开通充值！"),
    SERVER_NODE_NOT_CASH(700005,"该节点暂未开通提现！"),
    SERVER_NODE_NOT_TRANSFERS_BETWEEN_ERROR(700005,"暂未开通@@互转！"),
    SERVER_NODE_NOT_TRANSFERS_BETWEEN_ALL_ERROR(700005,"暂未开通内部互转！"),
    CAT_NOT_TRANSFERS_BETWEEN(700005,"该节点暂不支持CAT互转！"),
    USDT_NOT_TRANSFERS_BETWEEN(700005,"该节点暂不支持USDT互转！"),
    CAG_NOT_TRANSFERS_BETWEEN(700005,"该节点暂不支持CAG互转！"),

    SERVER_NODE_ACROSS_NODES_TRANSFERS_BETWEEN(700005,"当前节点暂不支持跨节点互转！"),
    SERVER_TRANSFERS_BETWEEN_AWAIT_OPEN(700005,"节点间互转功能等待开放！"),

    //
    CHECK_IS_SEA_PATROL_SERVER(700005,"目前只支持大航海计划同节点互转"),




    ;
    private Integer code;

    private String msg;
}
