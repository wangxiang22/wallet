package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.response.CurrencyInfoRes;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("t_wallet_info")
public class TWalletInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 客户id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 币种id
     */
    @TableField("currency_id")
    private Long currencyId;
    /**
     * 币种编码
     */
    @TableField("currency_code")
    private String currencyCode;
    /**
     * 币种地址-冗余-取掉关联关系
     */
    @TableField("currency_address")
    private String currencyAddress;

    /**
     * U盾提供的币种地址
     */
    @TableField(exist = false)
    private String udunCurrencyAddress;
    /**
     * 服务节点id
     */
    @TableField("server_node_id")
    private Long serverNodeId;
    /**
     * 余额-币种数量
     */
    @TableField("balance_num")
    private BigDecimal balanceNum;
    /**
     * 冻结币种数量
     */
    @TableField("frozen_num")
    private BigDecimal frozenNum;


    /**
     * 变动资产
     */
    @TableField(exist = false)
    private BigDecimal movableAssetsNum;


    @Override
    public String toString() {
        return "TWalletInfo{" +
        ", id=" + id +
        ", uid=" + uid +
        ", currencyId=" + currencyId +
        ", currencyCode=" + currencyCode +
        ", currencyAddress=" + currencyAddress +
        ", serverNodeId=" + serverNodeId +
        ", balanceNum=" + balanceNum +
        ", frozenNum=" + frozenNum +
        "}";
    }

    public CurrencyInfoRes currencyInfoRes(){
        CurrencyInfoRes res = new CurrencyInfoRes();
        res.setDollar(balanceNum);
        res.setCurrencyName(currencyCode);
        return res;
    }
}
