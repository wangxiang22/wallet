package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.*;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 中奖者表
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Data
@TableName("lottery_draw_prizer")
public class LotteryDrawPrizer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("uid")
    private Long uid;
    /**
     * 奖项
     */
    @TableField("prize")
    private String prize;
    /**
     * 奖金
     */
    @TableField("value")
    private String value;
    /**
     * 中奖时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 中奖人
     */
    private String username;


}
