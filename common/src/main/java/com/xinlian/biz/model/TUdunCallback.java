package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TUdunCallback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 地址
     */
    private String address;
    /**
     * 交易数量
     */
    private String amount;
    /**
     * 矿工费
     */
    private String fee;
    /**
     * 币种精度
     */
    private String decimals;
    @TableField("coinType")
    private String coinType;
    @TableField("mainCoinType")
    private String mainCoinType;
    @TableField("businessId")
    private String businessId;
    @TableField("blockHigh")
    private String blockHigh;
    private String status;
    @TableField("tradeId")
    private String tradeId;
    /**
     * 交易类型 1 充币回调 ; 2 提币回调
     */
    @TableField("tradeType")
    private String tradeType;
    private String txid;
    private String memo;
    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public String toString() {
        return "TUdunCallback{" +
        ", id=" + id +
        ", address=" + address +
        ", amount=" + amount +
        ", fee=" + fee +
        ", decimals=" + decimals +
        ", coinType=" + coinType +
        ", mainCoinType=" + mainCoinType +
        ", businessId=" + businessId +
        ", blockHigh=" + blockHigh +
        ", status=" + status +
        ", tradeId=" + tradeId +
        ", tradeType=" + tradeType +
        ", txid=" + txid +
        ", memo=" + memo +
        ", createTime=" + createTime +
        "}";
    }
}
