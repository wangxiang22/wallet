package com.xinlian.biz.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
public class TWalletTradeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包交易订单表
     */
    private Long id;
    /**
     * 客户id
     */
    private Long uid;
    private Long currencyId;
    /**
     * 币种编码
     */
    private String currencyCode;
    /**
     * 交易地址
     */
    private String tradeAddress;
    /**
     * 交易币种数量
     */
    private BigDecimal tradeCurrencyNum;
    /**
     * 预计费用
     */
    private BigDecimal tradeFee;
    /**
     * 矿工费用 - 接口返回
     */
    private BigDecimal minersFee;
    /**
     * 交易状态 申请 1 ；提交 2；等待回调 3；失败 4 ；成功  5
     */
    private Integer tradeStatus;

    //旧状态
    private Integer oldTradeStatus;
    /**
     * 交易类型 (充值：TOP_UP；提币：MENTION_MONEY )
     */
    private Integer tradeType;
    /**
     * 区块链交易哈希
     */
    private String txId;
    /**
     * 交易时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 描述
     */
    private String note;
    //收/付款方电话
    private String counterPartyMobile;
    private Long counterPartyUid;
    //收/付款方姓名
    private String counterPartyUserName;
    //失败原因
    private String failReason;
    //描述
    private String des;
    private Integer isin;
    //备注
    private String remark;

    /**查询条件**/
    public static final String params = "userName:用户名，UID:uid,提币地址:tradeAddress，serverNodeId:节点id,tradeStatus:提现状态值(selectOption后台提供固定值)，tradeCurrencyNumMax：提币数量最大值，tradeCurrencyNumMin：提币数量最小值，" +
            "queryCreatTimeStartDate：查询提现申请起止时间，queryCreatTimeEndDate：查询提现申请起止时间,queryDisposeCheckTimeStartDate：处理开始时间,queryDisposeCheckTimeEndDate：处理结束时间";

    //用户名
    private String userName;
    //节点id
    private Long serverNodeId;
    private String serverNodeName;
    //提现开启时间
    private String queryCreatTimeStartDate;
    //提现结束时间
    private String queryCreatTimeEndDate;
    //审核处理开启时间
    private String queryDisposeCheckTimeStartDate;
    //审核处理结束时间
    private String queryDisposeCheckTimeEndDate;
    private String icon;
    //审核处理时间
    private Date disposeCheckTime;
    //发起方
    private String tradeSystemCode;

    /**
     * 查询是 查询交易流水 - 就查所有的
     */
    private String queryType;

    private BigDecimal tradeCurrencyNumMax;

    private BigDecimal tradeCurrencyNumMin;

    /**交易流水查询条件**/
    public static final String flowParams =
            "userName:用户名,serverNodeId:节点id,:uid:UID,currencyCode:币种code /USDT:USDT/CAT:CAT/CAG:CAG/全部就不要传," +
            "tradeAddress:交易地址,queryCreatTimeStartDate：查询开始时间,queryCreatTimeEndDate：查询结束时间";


    /**交易流水查询条件**/
    public static final String rechargeParams =
            "uid:UID,currencyCode:币种code /USDT:USDT/CAT:CAT/CAG:CAG/全部就不要传,充值类型：des," +
                    "tradeAddress:交易地址,queryDisposeCheckTimeStartDate：处理开始时间,queryDisposeCheckTimeEndDate：处理结束时间";
}
