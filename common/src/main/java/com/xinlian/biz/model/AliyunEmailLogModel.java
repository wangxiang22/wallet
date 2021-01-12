package com.xinlian.biz.model;

import lombok.Data;

import java.util.Date;

/**
 * com.xinlian.biz.model
 *
 * @date 2020/2/11 11:40
 */
@Data
public class AliyunEmailLogModel {
    private Long id;
    private String senderEmailAddress;
    private String acceptorEmailAddress;
    private String emailCode;
    private Integer useType;
    private String useTypeDesc;
    private String emailResult;
    private Date createtime;

}

/**
 * `sender_email_address` varchar(50) DEFAULT NULL COMMENT '发送方邮箱地址',
 *   `acceptor_email_address` varchar(50) DEFAULT NULL COMMENT '接收方邮箱地址',
 *   `email_code` varchar(10) DEFAULT NULL COMMENT '邮箱验证码',
 *   `use_type` tinyint(4) DEFAULT NULL COMMENT 'code-useType 参照 - MailTemplateEnum 语义',
 *   `email_result` varchar(20) DEFAULT NULL COMMENT '当时返回的结果',
 *   `createtime` varchar(255) DEFAULT NULL COMMENT '创建时间',
 */
