package com.xinlian.common.enums;

public enum ErrorCode {
    REQ_SUCCESS(200, "请求成功"),
    REQ_ERROR(201, "请求失败"),
    OTHER_AREA(4444,"用户异地登录，请重新登录 "),
    SYS_ERROR(1, "系统级别错误"),
    SERVER_ERROR(2, "服务发生错误"),
    NOT_FOUND(400, "无法找到"),
    SERVER_EXCEPTRION(500, "服务处理异常"),
    TIME_OUT(504, "请求超时"),
    NOT_SUPPORT(505, "所请求操作不支持"),
    NOT_PERMISSION(403, "暂无权限"),
    DB_ERROR(599, "数据库发生错误"),
    JSON_FORMATE_EXCEPTION(1001, "JSON参数格式错误"),
    JSON_CONFIG_EXCEPTION(1002, "JSON配置错误"),
    API_CLOSED(1003, "API接口已停用"),
    ACCESS_FORBIDDEN(1008, "禁止访问"),
    EXCEED_LIMIT(1009, "超过访问次数限制"),
    BUSI_EXCEPTION(501, "业务异常"),
    INVALID_PARAMETER(502, "参数不合法"),
    CHECK_ERROR(503,"验证失败");
    ;

    private Integer code;
    private String des;

    ErrorCode(Integer code, String des) {
        this.code = code;
        this.des = des;
    }
    public Integer getCode(){return code;}
    public String getDes(){return des;}
}
