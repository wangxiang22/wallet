package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ServerNodeRes {
    private Long id;
    private Long parentId;//节点上级id
    private String parentIds;//节点所有上级id
    private String name;//节点名称
    private BigDecimal activeRequireMoney;//激活矿机需要数量（USDT）
    private String logoUrl;//节点logo图片地址
    private Integer registerStatus;//是否可以注册 - 0：不可以注册，1：可以注册
    private Integer loginStatus;//是否可以登录 - 0：不可以登录，1：可以登录
    private Integer rechargeStatus;//是否可以充值 - 0：不可以充值，1：可以充值
    private Integer cashStatus;//是否可以提现 - 0：不可以提现，1：可以提现
    private Integer bindRocketStatus;//是否可以绑定火箭交易所 - 0：不可以绑定，1：可以绑定
    private Integer displayOrder;//排列顺序
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private List<ServerNodeRes> next;//子节点所有信息
}
