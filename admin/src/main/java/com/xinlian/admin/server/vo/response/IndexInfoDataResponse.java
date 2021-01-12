package com.xinlian.admin.server.vo.response;

import lombok.Data;

/**
 * com.xinlian.admin.server.vo.response
 *
 * @date 2020/2/17 11:38
 */
@Data
public class IndexInfoDataResponse {
    //累计注册总数 -- 所有的统计uid
    private String grandTotalRegisterValue;
    //累计自然用户总数  -- 一个身份证号码算一个
    private String grandTotalIdNosValue;
    //所有已激活矿机用户总数
    private String activateTotalValue;
    //今日新增注册总数
    private String todayGrandRegisterValue;
    //今日自然用户总数  -- 一个身份证号码算一个
    private String todayGrandIdNosValue;
    //今日已激活矿机用户总数
    private String todayActivateValue;

}
