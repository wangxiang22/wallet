package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhangJun
 * @version V1.0  2020/4/26
 **/
@Data
@TableName("t_online_cumulative_duration")
public class TOnlineCumulativeDuration implements Serializable {
    private static final long serialVersionUID = -6449584055198799834L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String uid;

    private LocalDate date;

    @TableField("last_time")
    private Integer lastTime;
}
