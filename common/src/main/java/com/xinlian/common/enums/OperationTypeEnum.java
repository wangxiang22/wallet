package com.xinlian.common.enums;

/**
 * com.xinlian.common.enums
 *
 * @author by Song
 * @date 2020/2/19 16:10
 */
public enum OperationTypeEnum {

    SYSTEM_LOGIN(1, "系统登录"),

    REAL_NAME_AUDIT(2, "实名审核"),
    USER_FREEZE(3, "用户冻结"),
    WITHDRAW_AUDIT(4, "提现审核"),
    SERVER_NODE_ASSERT(5, "节点维护"),
    OTHER_OPERATE(6, "其他操作"),

    SEND_EMAIL_CODE(7, "发送邮箱验证码"),



    ;

    private Integer code;
    private String desc;

    OperationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static OperationTypeEnum toEnum(Integer code) {
        for (OperationTypeEnum item : OperationTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        StringBuilder str = new StringBuilder();
        str.append("the argument of ").append(code).append(" have no correspondence OperationTypeEnum enum!");
        throw new IllegalArgumentException(str.toString());
    }
}
