package com.xinlian.common.request;

import lombok.Data;

@Data
public class ChangePhoneReq {
    private Long uid;
    private String oldPhone;
    private String oldCode;

    private Integer countryCode;
    private String newPhone;
    private String newCode;

}
