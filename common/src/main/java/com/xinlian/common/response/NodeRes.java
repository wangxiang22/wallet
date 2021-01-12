package com.xinlian.common.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NodeRes {
    private Long id;//节点id
    private String name;//节点名称
    private BigDecimal activeRequireMoney;//激活矿机需要数量（USDT）
    private String logoUrl;//节点logo图片地址
    private Integer bindRocketStatus;//是否可以绑定火箭交易所 - 0：不可以绑定，1：可以绑定
}
