package com.xinlian.common.enums;

import lombok.Getter;

/**
 * @author Song
 * @date 2020-07-20 10:52
 * @description 国内国际code-desc
 */
@Getter
public enum NationTypeEnum {

    INLAND("INLAND","国内"),

    ABROAD("ABROAD","国际"),


    ;


    private String code;
    private String desc;

    NationTypeEnum(String code, String desc){
        this.code=code;
        this.desc=desc;
    }

}
