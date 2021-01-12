package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author WX
 * @since 2020-06-22
 */
@Data
@TableName("admin_rocket_to_wallet")
public class AdminRocketToWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("catUid")
    private Long catUid;
    @TableField("rocketUid")
    private Long rocketUid;
    private Long amount;
    private Integer status;
    @TableField("rocketPhone")
    private String rocketPhone;
    @TableField("coinName")
    private String coinName;
    @TableField("creatTime")
    private Date creatTime;
}
