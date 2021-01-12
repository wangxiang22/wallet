package com.xinlian.admin.biz.redis;

import org.springframework.stereotype.Component;

/**
 * Redis前缀
 */
@Component
public class RedisConstant {

    /**
     * app-redis-前缀
     */
    public static String APP_REDIS_PREFIX = "APP_WALLET_";
    /**
     * app-redis-前缀
     */
    public static String ADMIN_REDIS_PREFIX = "ADMIN_WALLET_";
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
     * 全部文章
     * */
    public static String QUERY_LIST_FOR_ARTICLE_KEY="_QUERY_LIST_FOR_ARTICLE_";
    /**
     * 10条文章
     * */
    public static String QUERY_LIST_FOR_TEN_ARTICLE_KEY="QUERY_LIST_FOR_TEN_ARTICLE_KEY";

    /**
     * app版本更新
     */
    public static String APP_VERSION="_:APP_VERSION";

    //
    public static String WITHDRAW_IS_CHECK_SMS_24 = "";


    /*****************************Redis KEY **********************************/
    public static final String REDIS_KEY_SESSION_MENU = "ADMIN_REDIS_SESSION_MENU";
    public static final String REDIS_KEY_SESSION_LABEL = "ADMIN_REDIS_SESSION_LABEL";
    public static final String REDIS_KEY_SESSION_ROLE = "ADMIN_REDIS_SESSION_ROLE";
    public static final String REDIS_KEY_SESSION_REQ_INTERFACE_URL = "ADMIN_REDIS_SESSION_REQ_INTERFACE_URL";

    public static final String REDIS_KEY_SESSION_USERID = "ADMIN_REDIS_SESSION_LOGIN_";

    //admin配置项前缀
    public static final String REDIS_KEY_SESSION_ADMIN_OPTION = "ADMIN_OPTION_";

    //后台登陆邮箱验证码code-key
    public static final String REDIS_KEY_LOGIN_EMAIL_CODE = "ADMIN_LOGIN_CODE_";

    //账单分类
    public static final String ADMIN_BILL_CLASSIFY = "ADMIN_BILL_CLASSIFY";

    //百度access_token
    public static final String BAIDU_ACCESS_TOKEN = "BAIDU_ACCESS_TOKEN";
}
