package com.xinlian.common.request;

import lombok.Data;

/**
 * com.xinlian.common.request
 *
 * @author by Song
 * @date 2020/2/13 12:11
 */
@Data
public class QueryEmailRequest {

    public static final String PARAMS = "serverNodeId:节点Id,loginUserName:节点客户登录用户名";
    private Long serverNodeId;
    private String loginUserName;
    private String sign;//md5签名


}
