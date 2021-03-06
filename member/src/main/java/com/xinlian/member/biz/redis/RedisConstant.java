package com.xinlian.member.biz.redis;

import org.springframework.stereotype.Component;

/**
 * Redis前缀
 */
@Component
public class RedisConstant {

    //admin配置项前缀
    public static final String REDIS_KEY_SESSION_ADMIN_OPTION = "ADMIN_OPTION_";
    //app-redis-前缀
    public static String APP_REDIS_PREFIX = "APP_WALLET_";
    public static String REDIS_KEY_SPLIT = "_";
    /**
     * 全部节点信息
     */
    public static String NODE_ALL_KEY = "NODE_ALL";
    /**
     * 除新大陆及其子节点以外的节点信息
     */
    public static String NODE_OTHER_KEY = "NODE_OTHER";
    /**
     * 新大陆及其子节点的信息
     */
    public static String NODE_XINDALU_KEY = "NODE_XINDALU";
    /**
     * 全部节点组装信息
     */
    public static String NODE_ASSEMBLY = "NODE_ASSEMBLY";
    /**
     * email验证码key
     */
    public static String EMAIL_CODE_KEY_PREFIX = APP_REDIS_PREFIX + "EMAIL_CODE_KEY_";
    /**
     * 全部文章
     * */
    public static String QUERY_LIST_FOR_ARTICLE_KEY="_QUERY_LIST_FOR_ARTICLE_";
    /**
     * 10条文章文章
     * */
    public static String QUERY_LIST_FOR_TEN_ARTICLE_KEY="QUERY_LIST_FOR_TEN_ARTICLE_KEY";

    public static String WITHDRAW_IS_CHECK_SMS_24 = "";

    /**
     * 国家字典List key
     */
    public static String COUNTRY_DIC_KEY= "COUNTRY_DIC_KEY";
    /**
     * 国家字典 单个
     */
    public static String COUNTRY_SINGLE_DIC_KEY= "COUNTRY_SINGLE_DIC_KEY";
    /**
     * 省份城市组合信息
     */
    public static String PROVINCE_CITY_KEY = "PROVINCE_CITY_KEY";
    /**
     * 用户激活状态与冻结状态
     */
    public static String USER_ACTIVE="userActive";

    /**
     * app版本更新
     */
    public static String APP_VERSION="_:APP_VERSION";
    
    /**
     * app版本更新-非强制更新已推送标识
     */
    public static String APP_VERSION_PUSHED="APP_VERSION_PUSHED";


    /**
     * 新版下发数量
     */
    public static String VERSION_COUNT_ISSUED = "VERSION_COUNT_ISSUED";


    /**
     * 新版下载数量
     */
    public static String VERSION_COUNT_INSTALL = "VERSION_COUNT_INSTALL";

    /**
     * 容许客户提现Uid keys字符串
     */
    public static String WITHDRAW_CUSTOMER_UID = RedisConstant.APP_REDIS_PREFIX + "WITHDRAW_CUSTOMER_UID";

    /**
     * 冻结周期配置前缀
     */
    public static String FREEZE_CYCLE_OPTION = RedisConstant.APP_REDIS_PREFIX + "FREEZE_CYCLE_OPTION_";

    /**
     * 注册节点正则表达式
     */
    public static String REGISTER_PATTERN = RedisConstant.APP_REDIS_PREFIX + "REGISTER_PATTERN";

    /**
     * 奖项前缀
     */
    public static String DrawPrize="drawPrize";
    /**
     * 获奖跑马灯
     */
    public static String DRAWER = "drawer";

    /**
     *挂单当日卖出总量
     */
    public static String TODAY_AMOUNT= "today_amount";
    /**
     * 挂单单次卖出量
     */
    public static String ONCE_AMOUNT= "once_amount";
}
