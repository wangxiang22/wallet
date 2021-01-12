package com.xinlian.common.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * 阿里云邮箱发送类型
 */
@Getter
public enum MailTemplateEnum {

    REGISTER_TYPE(1,"注册操作","注册"),
    OTHER_TYPE(2,"转账操作","转账"),
    BIND_TYPE(3,"绑定操作","绑定"),
    BIND_EXCHANGE_TYPE(4,"绑定交易所操作","绑定交易所"),
    CANCEL_BIND_EXCHANGE_TYPE(5,"取消绑定交易所操作",""),
    EXCHANGE_WALLET_TRADE_TYPE(6,"交易所钱包互转操作",""),
    FORGET_THE_LOGIN_PASSWORD(7,"忘记登录密码操作",""),
    FORGET_THE_PAY_PASSWORD(8,"忘记支付密码操作",""),
    ADMIN_SYSTEM_LOGIN_EMAIL_CODE(9,"登录后台操作",""),
    SEND_SMS_ERROR(-1,"发送短信验证码错误",""),
    DEF_EMAIL(-1,"找不到的使用类型邮件","错误"),
    SPOT_TRADE(11,"挂单交易","挂单交易"),
    PLEDGE_ERROR_SCHEDULE(12,"质押成功用户的信息传送给算力地球接口出现异常","质押成功用户的信息传送给算力地球接口出现异常"),
    ;

    private Integer code;
    private String des;

    private String emailContent;

    MailTemplateEnum(int code , String des,String emailContent){
        this.code=code;
        this.des=des;
        this.emailContent = emailContent;
    }


    public static String getEnumDesc(Integer code){
        return Stream.of(MailTemplateEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(DEF_EMAIL).getDes();
    }

    public static Integer getEnumCode(Integer code){
        return Stream.of(MailTemplateEnum.values()).filter(e -> e.getCode().equals(code)).findFirst().orElse(DEF_EMAIL).getCode();
    }

    public static void main(String[] args) {
        System.err.println(MailTemplateEnum.getEnumCode(112));
    }
}
