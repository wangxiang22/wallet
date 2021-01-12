package com.xinlian.common.request;

import lombok.Data;

import java.util.Date;

@Data
public class QueryOrderListReq extends BaseReq{
    private Long uid;//
    //商品名称
    private String goodsName;
    //购买数量区间-低值
    private Integer minAmount;
    //购买数量区间-高值
    private Integer maxAmount;
    //订单号
    private String orderNo;
    //手机号
    private String phone;
    //用户名
    private String userName;
    //状态
    private Integer status;
    //创建时间区间 - 低值
    private Date minCreateTime;
    //创建时间区间 - 高值
    private Date maxCreateTime;
    //是否导入
    private Integer isImport;

}


