package com.xinlian.common.request;

import com.xinlian.biz.model.TChangePhone;
import lombok.Data;

@Data
public class ChangePhoneIdcardReq extends TChangePhone {
    private String code;
}
