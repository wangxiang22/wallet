package com.xinlian.common.redis;

import com.xinlian.common.cachekey.CacheTemplateInterface;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.result.BizException;

public class RedisKeys {

    public static String SYMBOLIC_LINK = ":_";

    // admin配置项前缀
    public static final String REDIS_KEY_SESSION_ADMIN_OPTION = "ADMIN_OPTION_";

    public static final String REDIS_VENDOR_SMS = "APP_VENDOR_";

    public static final String REDIS_APP_SMS = "APP_WALLET_";

    // 获取大航海计划及其子节点的信息
    public static final String NODE_VOYAGE = "NODE_VOYAGE";

    public static String freezeUserKey(Long uid) {
        return "freeze:user:" + uid;
    }

    public static String freezeAdminUserKey(Long uid) {
        return "freeze:admin:user:" + uid;
    }

    public static String mailCodeKey(String mail) {
        return "register:code:" + mail;
    }

    public static String createSmsPhoneKey(int type, String phone) {
        switch (type) {
            case 4:
                return phone + "_WITH_DRAW";// 提币
            case 9:
                return phone + "BindEx";
            case 10:
                return phone + "delBind";
            case 11:
                return phone + "exWalletWithdraw";
            case 12:
                return phone + "phoneAllUser";
            case 13:
                return phone + "_UPDATE_COUNTRY";
            case 14:
                return phone + "_TRADE_BUY";// 购买交易key
            case 15:
                return phone + "_CAT_SELL";// 卖出cat
            case 16:
                return phone + "_BUY_GOODS";//购物
            case 810:
                return phone + "_BLOCKMALL_CERT";// 布鲁克商城哥伦布会员认证
            case 811:
                return phone + "_BLOCKMALL_PAY";// 布鲁克商城哥伦布钱包支付
            case 813:
                return phone + "_AFTER_LOGIN_BIND_PHONE";//登录后绑定手机号码
            default:
                return phone;

        }
    }

    public static String checkRegisterIsSendKey(String phone, int reqSmsType) {
        if (reqSmsType == 0 || reqSmsType == 1) {
            reqSmsType = 1;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("APP_WALLET:REGISTER_IS_SEND:" + reqSmsType + ":");
        sb.append(phone);
        return sb.toString();
    }

    public static String phoneKey(String phone) {
        return "query:code:" + phone;
    }

    public static String appVersionIssuedKey(String prefix, Integer appType, Integer id) {
        StringBuffer sb = new StringBuffer();
        sb.append(prefix).append(SYMBOLIC_LINK);
        sb.append(appType).append(SYMBOLIC_LINK);
        sb.append(id);
        return sb.toString();
    }

    public static String getUidByCurrencyAddress(final String prefix, final String currencyAddress) {
        return prefix.concat("SMART_CONTRACT_").concat(currencyAddress);
    }

    public static String getCurrencyAddressByUid(final String prefix, final Long uid) {
        return prefix.concat("SMART_CONTRACT_").concat(uid.toString());
    }

    public static String getSendEmailKey(final String prefix, final Integer useType, final String email) {
        Integer enumCode = MailTemplateEnum.getEnumCode(useType);
        if (enumCode.intValue() != useType.intValue()) {
            throw new BizException("发送邮箱验证码，检验参数出现异常，请稍后重试!");
        }
        return prefix.concat(email).concat("_").concat(useType.toString());
    }

    public static String getSendSmsKey(final CacheTemplateInterface cacheTemplateInterface) {
        return REDIS_APP_SMS.concat(cacheTemplateInterface.getCacheKey());
    }

    /**
     * 已支付订单号
     *
     * @param orderNo
     * @return
     */
    public static String blockmallOrderKey(String orderNo) {
        return "BLOCKMALL:ORDER:" + orderNo;
    }

    /**
     * 已支付订单详情
     *
     * @param orderNo
     * @return
     */
    public static String blockmallOrderDetailKey(String orderNo) {
        return "BLOCKMALL:ORDER:" + orderNo + ":DETAIL";
    }

    /**
     * 短时间内同一订单号防止重复请求
     *
     * @param orderNo
     * @return
     */
    public static String blockmallTempOrderKey(String orderNo) {
        return "BLOCKMALL:TEMPORDER:" + orderNo;
    }

    /**
     * 未支付订单号
     *
     * @param orderNo
     * @return
     */
    public static String blockmallUnPayOrderKey(String orderNo) {
        return "BLOCKMALL:UNPAYORDER:" + orderNo;
    }

    /**
     * 未过期订单号
     *
     * @param orderNo
     * @return
     */
    public static String blockmallUnTimeoutOrderKey(String orderNo) {
        return "BLOCKMALL:UNTIMEOUTORDER:" + orderNo;
    }

    /**
     * 支付结果推送失败，重试key
     *
     * @param orderNo
     * @return
     */
    public static String repeatPayCallbackErrorKey(String orderNo) {
        return "BLOCKMALL:repeatPayCallbackError:" + orderNo;
    }
}
