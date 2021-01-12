package com.xinlian.common.request;

import lombok.Data;

/**
 * com.xinlian.common.request
 * 需要登录情况下发送短信请求
 * @author by Song
 * @date 2020/7/9 07:07
 *
 */
@Data
public class NeedLoginSendSmsReq {

    //区域
    private Integer countryCode;
    //节点
    private String phone;
    //
    private int type;

}
