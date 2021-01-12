package com.xinlian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 系统渠道枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SystemChannelEnum {

    APP_ANDROID("ANDROID", "安卓"),

    APP_IOS("IOS", "苹果"),

    APP_H5("H5", "页面H5"),

    ;


    String channelCode;

    String channelDesc;
}
