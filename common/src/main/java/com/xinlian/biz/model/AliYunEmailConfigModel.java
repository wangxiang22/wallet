package com.xinlian.biz.model;

import lombok.Data;

/**
 * com.xinlian.biz.model
 *
 * @date 2020/2/9 19:20
 */
@Data
public class AliYunEmailConfigModel {

    private Long id;
    private String emailAddress;
    private String accessKeyId;
    private String accessSecret;
    //发信域名
    private String sendDomain;
    //初始化发送量 - 小于这个发送量 - 走下一个
    private Integer sendInitNum;
    //状态
    private Integer status;
    //使用排序
    private Integer useSort;

}

/**
 * `id` bigint(20) NOT NULL,
 *   `email_address` varchar(50) DEFAULT NULL COMMENT 'email地址',
 *   `access_key_id` varchar(50) DEFAULT NULL COMMENT 'key_id',
 *   `access_secret` varchar(50) DEFAULT NULL COMMENT '密钥',
 *   `send_domain` varchar(50) DEFAULT NULL COMMENT '发信域名',
 *   `send_init_num` int(11) DEFAULT NULL COMMENT '每天初始化发送量',
 *   `status` int(11) DEFAULT NULL COMMENT '状态 1 ： 在用 ； 2：禁用',
 *   `use_sort` int(11) DEFAULT NULL COMMENT '使用排序',
 */
