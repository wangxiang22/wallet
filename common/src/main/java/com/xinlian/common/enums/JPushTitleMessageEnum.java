package com.xinlian.common.enums;

import lombok.Getter;

@Getter
public enum JPushTitleMessageEnum {
    CASH_SUCCESS("提币成功","您有一笔XXX的提币已经到账，请注意查收！"),
    CASH_AUDIT_REFUSED("提币审核未通过","您有一笔XXX的提币没有通过审核，请知晓！"),
    CASH_FAIL("提币失败","您有一笔XXX的提币未成功，请知晓！"),
    RECHARGE_SUCCESS("充值到账","您有一笔XXX的充值已到账，请知晓！"),
    RECHARGE_FAIL("充值失败","您有一笔XXX的充值未成功，请知晓！"),
    TRANSFER_SUCCESS("互转到账","您有一笔XXX的互转已成功，请知晓！"),//互转成功给钱款接收方发送通知
    ;

    private String title;
    private String msg;

    JPushTitleMessageEnum(String title,String msg){
        this.title=title;
        this.msg=msg;
    }

    /**
     * 拼接金额及单位到msg中
     * @param msg msg模板
     * @param replaceMsg 需要拼接到模板中的字段
     * @return 返回拼接后的msg
     */
    public static String getReplaceMsg(String msg,String replaceMsg) {
        return msg.replace("XXX",replaceMsg);
    }
}
