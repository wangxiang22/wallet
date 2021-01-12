package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@TableName("t_wallet_trade_order")
public class TWalletTradeOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 钱包交易订单表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 客户id
     */
    @TableField(value = "uid")
    private Long uid;
    @TableField(value = "currency_id")
    private Long currencyId;
    /**
     * 币种编码
     */
    @TableField(value = "currency_code")
    private String currencyCode;
    /**
     * 交易地址
     */
    @TableField(value = "trade_address")
    private String tradeAddress;
    /**
     * 交易币种数量
     */
    @TableField(value = "trade_currency_num")
    private BigDecimal tradeCurrencyNum;
    /**
     * 预计费用
     */
    @TableField(value = "trade_fee")
    private BigDecimal tradeFee;
    /**
     * 矿工费用 - 接口返回
     */
    @TableField(value = "miners_fee")
    private BigDecimal minersFee;
    /**
     * 交易状态 申请 1 ；提交 2；等待回调 3；失败 4 ；成功  5
     */
    @TableField(value = "trade_status")
    private Integer tradeStatus;

    //旧状态
    @TableField(exist = false)
    private Integer oldTradeStatus;
    /**
     * 交易类型 (充值：TOP_UP；提币：MENTION_MONEY )
     */
    @TableField(value = "trade_type")
    private Integer tradeType;
    /**
     * 区块链交易哈希
     */
    @TableField(value = "tx_id")
    private String txId;
    /**
     * 交易时间
     */
    @TableField(value = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
    /**
     * 描述
     */
    @TableField(exist = false)
    private String note;
    //收/付款方电话
    @TableField(value = "counter_party_mobile")
    private String counterPartyMobile;
    @TableField(value = "counter_party_uid")
    private Long counterPartyUid;
    //收/付款方姓名
    @TableField(value = "counter_party_user_name")
    private String counterPartyUserName;
    //失败原因
    @TableField(value = "fail_reason")
    private String failReason;
    //描述
    private String des;
    @TableField("isin")
    private Integer isin;
    //备注
    @TableField(exist = false)
    private String remark;

    @TableField(exist = false)
    /**查询条件**/
    public static final String params = "userName:用户名，UID:uid,提币地址:tradeAddress，serverNodeId:节点id,tradeStatus:提现状态值(selectOption后台提供固定值)，tradeCurrencyNumMax：提币数量最大值，tradeCurrencyNumMin：提币数量最小值，" +
            "queryCreatTimeStartDate：查询提现申请起止时间，queryCreatTimeEndDate：查询提现申请起止时间,queryDisposeCheckTimeStartDate：处理开始时间,queryDisposeCheckTimeEndDate：处理结束时间";

    @TableField(exist = false)
    //用户名
    private String userName;
    //节点id
    @TableField(exist = false)
    private Long serverNodeId;
    @TableField(exist = false)
    private String serverNodeName;
    //提现开启时间
    @TableField(exist = false)
    private String queryCreatTimeStartDate;
    //提现结束时间
    @TableField(exist = false)
    private String queryCreatTimeEndDate;
    //审核处理开启时间
    @TableField(exist = false)
    private String queryDisposeCheckTimeStartDate;
    //审核处理结束时间
    @TableField(exist = false)
    private String queryDisposeCheckTimeEndDate;
    @TableField(exist = false)
    private String icon;
    //审核处理时间
    @TableField(exist = false)
    private Date disposeCheckTime;
    //发起方
    @TableField(value = "trade_system_code")
    private String tradeSystemCode;

    /**
     * 查询是 查询交易流水 - 就查所有的
     */
    @TableField(exist = false)
    private String queryType;

    @TableField(exist = false)
    private BigDecimal tradeCurrencyNumMax;

    @TableField(exist = false)
    private BigDecimal tradeCurrencyNumMin;

    /**交易流水查询条件**/
    @TableField(exist = false)
    public static final String flowParams =
            "userName:用户名,serverNodeId:节点id,:uid:UID,currencyCode:币种code /USDT:USDT/CAT:CAT/CAG:CAG/全部就不要传," +
            "tradeAddress:交易地址,queryCreatTimeStartDate：查询开始时间,queryCreatTimeEndDate：查询结束时间";


    /**交易流水查询条件**/
    @TableField(exist = false)
    public static final String rechargeParams =
            "uid:UID,currencyCode:币种code /USDT:USDT/CAT:CAT/CAG:CAG/全部就不要传,充值类型：des," +
                    "tradeAddress:交易地址,queryDisposeCheckTimeStartDate：处理开始时间,queryDisposeCheckTimeEndDate：处理结束时间";
}
