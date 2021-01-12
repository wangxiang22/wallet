package com.xinlian.member.biz.smsvendor.sendcloud;

import lombok.Data;

/**
 * @author Song
 * @date 2020-07-10 14:07
 * @description 搜孤旗下@武汉闪达科技有线公司-国际短信配置
 */
@Data
public class SendCloudAbroadSmsConfig extends SendCloudBaseSmsConfig{


    //业务类型，"0"代表国内短信，"2"代表国际短信，默认国内短信
    private String msgType = "2";


}
