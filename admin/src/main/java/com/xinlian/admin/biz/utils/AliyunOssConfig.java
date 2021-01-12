package com.xinlian.admin.biz.utils;

import lombok.Data;

@Data
public class AliyunOssConfig {

    private String ossEndpoint ;
    private String ossKeyId ;
    private String ossKeySecret;
    private String ossUrlBucket;
    //上传目录
    private String ossFolder;

}
