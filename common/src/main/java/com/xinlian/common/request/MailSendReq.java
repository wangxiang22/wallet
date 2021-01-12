package com.xinlian.common.request;

import lombok.Data;

@Data
public class MailSendReq {
    private String email;
    private Long nodeId;
    private Integer useType; // 1.注册类型，2.交易类型 3.cat买卖 4.

    //纯邮箱用户专用 md5签名
    private String sign;
    //极验
    private String asSubPre;

}
