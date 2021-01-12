package com.xinlian.common.contants;

public interface GlobalConstant {

    interface ResponseCode{
        //用户名或密码错误
        Integer LOGIN_ERROR = 10;
        //账号冻结
        Integer ACCOUNT_FREEZING = 20;
        //无访问权限
        Integer NO_ACCESS = 30;
        //请求成功
        Integer SUCCESS = 200;
        //操作失败
        Integer FAIL = 201;
        //请求参数不合法
        Integer PARAM_ERROR = 400;
        //未登录
        Integer RE_LOGIN = 401;
        //系统异常
        Integer SYS_ERROR = 500;
        //异地登录
        Integer OTHER_AREA_LOGIN=4444;

    }

    interface AccountStatus{
        //普通
        int VIP0 = 1;
        //冻结
        int VIP1 = 2;
        //会员
        int VIP2 = 3;
    }


}
