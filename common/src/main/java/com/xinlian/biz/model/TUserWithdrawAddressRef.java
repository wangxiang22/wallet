package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.response.WithdrawAddressRes;
import com.xinlian.common.utils.DateFormatUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("t_user_withdraw_address_ref")
public class TUserWithdrawAddressRef implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 客户表主键id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 转出地址币种id
     */
    @TableField("to_currency_id")
    private Long toCurrencyId;

    @TableField("to_currency_code")
    private String toCurrencyCode;

    @TableField("to_currency_name")
    private String toCurrencyName;
    /**
     * 转出地址address
     */
    @TableField("to_currency_address")
    private String toCurrencyAddress;

    @TableField("to_address_name")
    private String toAddressName;

    /**
     * 转出币种地址对应节点id 内部地址有值 外部地址为-1
     */
    @TableField("server_node_id")
    private Long serverNodeId;

    @TableField("create_time")
    private Date createTime;

    /**
     * 删除状态 0未删除
     */
    @TableField("is_del")
    private Integer isDel;



    @Override
    public String toString() {
        return "TUserWithdrawAddressRef{" +
        ", id=" + id +
        ", uid=" + uid +
        ", toCurrencyId=" + toCurrencyId +
         ", toCurrencyCode=" + toCurrencyCode +
        ", toCurrencyAddress=" + toCurrencyAddress +
        ", serverNodeId=" + serverNodeId +
        ", isDel=" + isDel +
        ", createTime=" + createTime +
        "}";
    }

    public WithdrawAddressRes withdrawAddressRes(){
        WithdrawAddressRes res = new WithdrawAddressRes();
        res.setCoinId(toCurrencyId);
        res.setAddress(toCurrencyAddress);
        res.setCoinCode(toCurrencyCode);
        res.setCoinName(toCurrencyName);
        res.setInputtime(DateFormatUtil.dateToLong(createTime));
        res.setId(id);
        res.setAddressName(toAddressName);
        return res;
    }
}
