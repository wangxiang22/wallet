package com.xinlian.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainOwnerReq {
    private Long uid;//用户id
    private String auth_name;//用户实名姓名
    private String auth_sn;//用户实名证件号
    private String email;//用户邮箱
    private String url;//链权人证书地址
}
