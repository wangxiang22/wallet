package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wx
 * @since 2020-03-25
 */
@TableName("t_recommend")
@Data
public class TRecommend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片地址
     */
    private String thumb;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 链接地址
     */
    private String url;

    /**
     * 创建时间
     */
    @TableField("creat_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date creatTime;
    @TableField("type")
    private Integer type;
    @TableField("is_hidden")
    private Integer isHidden;

    private String code;
}
