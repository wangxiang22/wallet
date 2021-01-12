package com.xinlian.admin.server.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * com.xinlian.admin.server.vo
 *
 * @author by Song
 * @date 2020/2/17 20:29
 */
@Data
public class WalletTradeFlowVo {

    private static final long serialVersionUID = 1L;

    //客户uid
    private Long uid;
    //客户姓名
    private String userName;
    //交易主键id
    private Long tradeId;
    //币种id
    private Long currencyId;
    //币种code
    private String currencyCode;
    //交易金额
    private BigDecimal tradeCurrencyNum;
    //交易地址
    private String tradeAddress;
    //交易类型
    private Integer tradeType;
    //交易状态 - 状态码
    private Integer tradeStatus;
    //交易状态 - 中文描述
    private String tradeStatusName;
    //交易描述
    private String des;
    //订单开始日期
    private String createTime;
    //txId - 公链交易id
    private String txId;
    //失败原因
    private String failReason;
    /**
     * 预计费用
     */
    private String tradeFee;
    /**
     * 矿工费用 - 接口返回
     */
    private String minersFee;
    //节点id
    private Long serverNodeId;
    //节点名称
    private String serverNodeName;
    //备注
    private String remark;
    //收/付款 uid
    private Long counterPartyUid;
}
