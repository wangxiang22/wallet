package com.xinlian.common.request;

import lombok.Data;

@Data
public class FindExchangeBindStateReq {
    private Long uid;//用户id
    private String userName;//用户名
    private Integer node;//节点
    private String phone;//电话号码
    private Long rocketUid;
    private String rocketPhone;
    private Long pageNum;
    private Long pageNum2;
}
