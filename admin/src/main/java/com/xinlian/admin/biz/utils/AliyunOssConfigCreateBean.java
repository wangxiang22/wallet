package com.xinlian.admin.biz.utils;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliyunOssConfigCreateBean {

    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Bean
    public AliyunOssConfig getAliyunOssConfig(){
        try {
            AliyunOssConfig aliyunOssConfig = adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.ADMIN_OSS.getBelongsSystemCode(), AliyunOssConfig.class);
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~:{}",JSONObject.toJSONString(aliyunOssConfig));
            return aliyunOssConfig;
        }catch (Exception e){
            return null;
        }
    }


}
