package com.xinlian.common.enums;

import lombok.Getter;

/**
 * com.xinlian.common.enums
 * @author by Song
 * @date 2020/3/14 08:45
 */
@Getter
public enum AdminOptionsBelongsSystemCodeEnum {

    BASIC_CONFIG("BASIC_CONFIG","基础配置项，走option_name为redis的key存储"),

    APP_ACT("APP_ACT","APP活动"),

    ADMIN_OSS("ADMIN_OSS","ADMIN上传图片OSS配置"),

    ADMIN_OSS_UPLOAD("ADMIN_OSS_UPLOAD","ADMIN上传移动端安装包OSS配置"),

    APP_BOOT_SCREEN("APP_BOOT_SCREEN","APP启动界面配置"),

    APP_CAL_EARTH("APP_CAL_EARTH","算力地球链接配置"),

    SYSTEM_SWITCH("SYSTEM_SWITCH","全局配置"),

    APP_PLEDGE("APP_PLEDGE","矿池质押金额"),

    LOTTERY_DRAW("LOTTERY_DRAW","抽奖配置"),

    APP_AUTH_AGE("APP_AUTH_AGE","实名认证年龄限制区间配置"),

    ORDER_TIME_OUT("ORDER_TIME_OUT","订单过期时间"),

    LIMIT_WITHDRAW_UIDS("LIMIT_WITHDRAW_UIDS","限制转账-提币uids集合"),

    LIMIT_WITHDRAW_CURRENCY("LIMIT_WITHDRAW_CURRENCY","限制转账-提币币种id集合"),

    LIMIT_REGISTER_SERVER_NODE_ID("LIMIT_REGISTER_SERVER_NODE_ID","限制注册serverNodeId集合"),

    SMS_SENDER_CODE("SMS_SENDER_CODE","国内短信发送方选择"),

    ABROAD_SMS_SENDER_CODE("ABROAD_SMS_SENDER_CODE","国际短信发送方选择"),

    SMS_HOUR_ERROR_NUM("SMS_HOUR_ERROR_NUM","短信1小时错误短信次数"),

    SMS_DAY_ERROR_NUM("SMS_DAY_ERROR_NUM","短信1天错误短信次数"),

    SMS_HOUR_LIMIT_SEND_NUM("SMS_HOUR_LIMIT_SEND_NUM","短信一小时限制发送量"),

    SMS_HALF_DAY_LIMIT_SEND_NUM("SMS_HALF_DAY_LIMIT_SEND_NUM","短信12小时限制发送量"),

    APP_RSA_PRIVATE_KEY("APP_RSA_PRIVATE_KEY","APP_RSA私钥"),

    APP_RSA_GRAY_PRIVATE_KEY("APP_RSA_GRAY_PRIVATE_KEY","准生产TestAPP_RSA私钥"),

    H5_RSA_PRIVATE_KEY("H5_RSA_PRIVATE_KEY","H5_RSA私钥"),

    SPOT_NODE_TRADE("SPOT_NODE_TRADE","是否开启节点互转"),

    SEND_EMAIL_TRUE("SEND_EMAIL_TRUE","是否可正常发送邮箱"),

    PHONE_WHITE_LIST("PHONE_WHITE_LIST","手机白名单"),

    REQUEST_URI_LIMIT_NUMBER("REQUEST_URI_LIMIT_NUMBER","请求uri限制次数"),
    REQUEST_URI_LIMIT_PERIOD("REQUEST_URI_LIMIT_PERIOD","请求uri限制周期"),

    SEA_PATROL_TRADE_END_TIME("SEA_PATROL_TRADE_END_TIME","大航海内部互转结束时间"),
    SEA_PATROL_TRADE_START_TIME("SEA_PATROL_TRADE_START_TIME","大航海内部互转开始时间"),
    BUY_GOODS_MASK_MONEY_USDT("BUY_GOODS_MASK_MONEY_USDT","钱包内部购买口罩价格设置"),

    APP_TRIGGER_RECORD_AMOUNT("APP_TRIGGER_RECORD_AMOUNT","触发不同币种写入资金变动流水记录表的金额"),
    APP_TRIGGER_POP_AMOUNT("APP_TRIGGER_POP_AMOUNT","不同币种触发提示弹窗的金额"),

    TRC_REQUEST_ENABLE_IP_LIST("TRC_REQUEST_ENABLE_IP_LIST","TRC_USDT允许请求IP,-1不限制"),

    ;

    private String belongsSystemCode;
    private String belongsSystemDesc;

    AdminOptionsBelongsSystemCodeEnum(String belongsSystemCode, String belongsSystemDesc){
        this.belongsSystemCode=belongsSystemCode;
        this.belongsSystemDesc=belongsSystemDesc;
    }

    public static String getDesc(){
        StringBuffer stringBuffer = new StringBuffer();
        for(AdminOptionsBelongsSystemCodeEnum enmus :AdminOptionsBelongsSystemCodeEnum.values()){
            stringBuffer.append(enmus.getBelongsSystemCode()+":"+enmus.getBelongsSystemDesc());
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        System.err.println(getDesc());
    }
}
