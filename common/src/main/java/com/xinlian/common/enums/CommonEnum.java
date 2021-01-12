package com.xinlian.common.enums;

public enum CommonEnum {
    BALANCE_NOT_ENOUGH(1,"余额不足"),
    PASSWORD_NOT_MATCH(2,"密码错误"),
    PAY_PASSWORD_NOT_MATCH(3,"支付密码错误"),
    NODE_NOT_OPEN(4,"激活尚未开放"),
    ACTIVATE_OK(5,"激活成功"),
    ALREADY_ACTIVATE(6,"矿机已激活无法重复激活"),
    AUTH_NOT_FOUND(7,"无用户实名信息"),
    UPDATE_OK(8,"更新成功"),
    CODE_OUT_TIME(9,"验证码已过期"),
    DEL_BIND_OK(10,"解绑成功"),
    CODE_ERROR(11,"验证码错误"),
    CODE_NULL(12,"验证码为空"),
    PARAM_NO(13,"非法参数"),
    NO_TIME_TRADE(14,"没有转账次数"),
    NO_AMOUNT_OF_MONEY(15,"超过当日可提现总金额"),
    OUT_OF_SINGLE_TRADE(16,"单次充提数量超过限制"),
    NO_PROMISION_EX_TO_WALLET(17,"交易所转入钱包未开放"),
    NO_PROMISION_WALLET_TO_EX(18,"钱包转入交易所未开放"),
    FAILED_TO_TRADE(19,"交易失败"),
    CAN_NOT_BIND_EXCHANGE_BY_NODE(20,"当前节点未开放绑定交易所"),
    OUT_OF_TODAY_DESPOIT(21,"超过当日可充值总金额"),
    PLEDGE_FAIL(22,"支付失败"),
    PAY_SUCCESS_WAIT_AUDIT(23,"支付成功，请耐心等待审核！"),
    ;

    private Integer code;
    private String des;

    CommonEnum(int code ,String des){
        this.code=code;
        this.des=des;
    }
    public int getCode() {
        return code;
    }
    public String getDes() {
        return des;
    }
}
