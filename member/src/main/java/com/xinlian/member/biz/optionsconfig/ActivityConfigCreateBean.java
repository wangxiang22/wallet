package com.xinlian.member.biz.optionsconfig;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ActivityConfigCreateBean {

    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Bean
    public ActivityConfig getActivityCreateBean(){
        try {
            ActivityConfig activityConfig =
                    adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_ACT.getBelongsSystemCode(), ActivityConfig.class);
            log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~:{}", JSONObject.toJSONString(activityConfig));
            return activityConfig;
        }catch (Exception e){
            return null;
        }
    }
}
