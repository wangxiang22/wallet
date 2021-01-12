package com.xinlian.member.server.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ServerNodeVo {
    private Long id;//节点id
    private String name;//节点名称
    private String nickname;//节点别名:大航海系列的节点此字段为英文名
    private BigDecimal activeRequireMoney;//激活矿机需要数量（USDT）
    private String logoUrl;//节点logo图片地址
    private Integer registerStatus;//是否可以注册 - 0：不可以注册，1：可以注册
    private Integer loginStatus;//是否可以登录 - 0：不可以登录，1：可以登录
//    private Integer bindRocketStatus;//是否可以绑定火箭交易所 - 0：不可以绑定，1：可以绑定
    private List<ServerNodeVo> next;
}
