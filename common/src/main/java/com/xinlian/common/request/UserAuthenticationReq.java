package com.xinlian.common.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthenticationReq {
    private Long uid;//用户uid
    private String auth_name;//用户真实姓名
    private String auth_sn;//身份证号
    private String sfzzm;//身份证正面
    private String sfzfm;//身份证反面
    private String scsfz;//手持身份证
    private Integer from;//参数为： 1：国内扫描方式 2：其他地区方式
    private String userName;
    private Long node;//用户节点
}
