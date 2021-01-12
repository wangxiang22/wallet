package com.xinlian.member.biz.smsvendor.aliyun;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-11 10:55
 * @description
 */
@Data
public class AliyunSmsResult {

    private String Message;

    private String RequestId;

    private String BizId;

    private String code;
}
