package com.xinlian.common.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

@Data
public class NewsArticleDto{


    private Long id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章地址
     */
    private String url;
    /**
     * 图片地址
     */
    private String thumb;
    /**
     * 是否置顶
     */
    private Integer topStatus;
    /**
     * 创建时间
     */
    private Long inputTime;
    /**
     * 浏览数
     */
    private Long hits;

    /**
     * 是否可以显示 0，不显示，1，显示
     */
    private Integer status;

}
