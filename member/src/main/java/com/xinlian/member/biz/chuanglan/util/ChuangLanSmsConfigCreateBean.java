package com.xinlian.member.biz.chuanglan.util;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.VendorSmsConfigUtil;
import com.xinlian.common.enums.VendorSmsConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * com.xinlian.member.biz.chuanglan.util
 *
 * @author by Song
 * @date 2020/3/18 23:49
 */
@Slf4j
@Configuration
public class ChuangLanSmsConfigCreateBean {

    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;

    @Bean
    public ChuangLanInlandSmsConfig getInlandSmsConfig(){
        try {
            ChuangLanInlandSmsConfig chuangLanInlandSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_INLAND_SMS.getBelongsSystemCode(), ChuangLanInlandSmsConfig.class);
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~:{}", JSONObject.toJSONString(chuangLanInlandSmsConfig));
            return chuangLanInlandSmsConfig;
        }catch (Exception e){
            return null;
        }
    }

    @Bean
    public ChuangLanAbroadSmsConfig getAbroadSmsConfig(){
        try {
            ChuangLanAbroadSmsConfig chuangLanAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_ABROAD_SMS.getBelongsSystemCode(), ChuangLanAbroadSmsConfig.class);
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~:{}", JSONObject.toJSONString(chuangLanAbroadSmsConfig));
            return chuangLanAbroadSmsConfig;
        }catch (Exception e){
            return null;
        }
    }
}
