package com.xinlian.biz.model;

import lombok.Data;

import java.util.Date;

/**
 * com.xinlian.biz.model
 *
 * @date 2020/2/11 11:40
 */
@Data
public class SmsLogModel {
    private Long id;
    private String sender;
    private String acceptorPhone;
    private String smsCode;
    private Integer useType;
    private String useTypeDesc;
    private String sendResult;
    private String smsRedisKey;
    private Long uid;
    private Date createtime;

}

/**
 *   `id` bigint(20) NOT NULL AUTO_INCREMENT,
 *   `sender` varchar(50) DEFAULT NULL COMMENT '发送方',
 *   `acceptor_phone` varchar(50) DEFAULT NULL COMMENT '接收方手机号码',
 *   `sms_code` varchar(10) DEFAULT NULL COMMENT '邮箱验证码',
 *   `use_type` tinyint(4) DEFAULT NULL COMMENT 'code-useType 参照 - 无参照语义',
 *   `use_type_desc` varchar(50) DEFAULT NULL COMMENT '验证码用途desc',
 *   `send_result` varchar(100) DEFAULT NULL COMMENT '当时返回的结果及信息',
 *   `createtime` varchar(255) DEFAULT NULL COMMENT '创建时间',
 */
