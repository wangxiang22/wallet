package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

@TableName("t_address_pool")
@Data
public class TAddressPool implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 链地址
     */
    @TableField("address")
    private String address;

    /**
     * 消费状态 -1未消费 1 消费中 2 消费
     */
    @TableField("status")
    private Integer status;

    @TableField("status")
    private Integer oldStatus;

    @Override
    public String toString() {
        return "TAddressPool{" +
        ", id=" + id +
        ", address=" + address +
        ", status=" + status +
        "}";
    }
}
