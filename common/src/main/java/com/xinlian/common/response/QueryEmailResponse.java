package com.xinlian.common.response;

import lombok.Data;

/**
 * com.xinlian.common.response
 *
 * @author by Song
 * @date 2020/2/13 13:16
 */
@Data
public class QueryEmailResponse {

    private Long serverNodeId;
    private String userName;
    private String phone;
    private String email;
    //国家区号
    private Integer countryCode;
}
