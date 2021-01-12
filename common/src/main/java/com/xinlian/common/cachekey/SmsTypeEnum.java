package com.xinlian.common.cachekey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * com.xinlian.common.cachekey
 *
 * @author by Song
 * @date 2020/7/8 23:45
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SmsTypeEnum implements CacheTemplateInterface {





    /****/
    SEA_PATROL_REGISTER_BIND_MOBILE(20200812,"SEA_PATROL_REGISTER_BIND_MOBILE","大航海注册绑定手机")

    ;

    Integer code;

    String cacheKey;

    String desc;







}
