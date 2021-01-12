package com.xinlian.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChainOwnerUserRes {
    private Long uid;//用户id
    private String email;//用户邮箱
    private String username;//用户名
    private String phone;//手机号码
    private String auth_name;//用户实名姓名
    private String auth_sn;//用户实名证件号
}
