package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * 容许提现usdt客户表
 * </p>
 *
 * @since 2020-05-25
 */
@Data
@TableName("t_withdraw_customer")
public class WithdrawCustomerModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 客户uid
     */
    private Long uid;
    /**
     * 能否提现状态 1 提现 ; 2 不能提现
     */
    @TableField("withdraw_status")
    private Integer withdrawStatus;
    /**
     * 容许提币数量
     */
    @TableField("allow_withdraw_num")
    private BigDecimal allowWithdrawNum;

    private Date createtime;

    @Override
    public String toString() {
        return "TWithdrawCustomer{" +
        ", id=" + id +
        ", uid=" + uid +
        ", withdrawStatus=" + withdrawStatus +
        ", allowWithdrawNum=" + allowWithdrawNum +
        ", createtime=" + createtime +
        "}";
    }
}
