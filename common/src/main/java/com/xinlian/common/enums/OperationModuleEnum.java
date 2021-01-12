package com.xinlian.common.enums;

/**
 * com.xinlian.common.enums
 *
 * @author by Song
 * @date 2020/2/19 11:46
 */
public enum OperationModuleEnum {

    LOGIN(1, "登录"),
    SYSTEM_MANAGE(2, "系统管理"),
    NODE_MANAGE(3, "节点管理"),
    NEWS_MANAGE(4, "新闻管理"),
    CUSTOMER_REAL_MANAGE(5,"客户实名管理"),
    CUSTOMER_FREEZE_MANAGE(6,"客户冻结管理"),
    CUSTOMER_INFO_MANAGE(7,"客户信息管理"),
    WITHDRAW_TRADE_MANAGE(8,"提币交易审核管理"),
    ADMIN_ROLE_MANAGE(9,"权限管理"),
    CURRENCY_MANAGE(10,"币种管理"),
    BILL_STATISTICS_MANAGE(11,"账单统计管理"),
    NEW_ORDER_MANAGE(12,"订单中心管理"),
    ;

    private Integer code;
    private String desc;

    OperationModuleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static OperationModuleEnum toEnum(Integer code) {
        for (OperationModuleEnum item : OperationModuleEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        StringBuilder str = new StringBuilder();
        str.append("the argument of ").append(code).append(" have no correspondence OperationLogModelEnum enum!");
        throw new IllegalArgumentException(str.toString());
    }
}
