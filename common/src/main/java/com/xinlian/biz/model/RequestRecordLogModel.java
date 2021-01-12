package com.xinlian.biz.model;

import lombok.Data;

import java.util.Date;

/**
 * com.xinlian.biz.model
 *
 * @date 2020/7/31 15:40
 */
@Data
public class RequestRecordLogModel {
    private Long id;
    private String requestUrl;
    private Long taskTime;
    private String jvmParam;
    private String requestIp;
    //内网ip
    private String serverIntranetIp;
    //所属系统
    private String belongSystem;
    private Long uid;
    private Date createtime;

}


