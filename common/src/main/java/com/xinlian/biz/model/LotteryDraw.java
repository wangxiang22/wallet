package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.*;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Data
@TableName("lottery_draw")
public class LotteryDraw implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 奖项
     */
    @TableField("prize")
    private String prize;
    /**
     * 奖值
     */
    @TableField("value")
    private String value;

    /**
     * 奖项code
     */
    private String code;
    /**
     * 活动内容
     */
    private String des;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * num
     */
    private Integer num;

}
