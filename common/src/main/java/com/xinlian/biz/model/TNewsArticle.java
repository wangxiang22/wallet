package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_news_article")
public class TNewsArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 关键字
     */
    private String keywords;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章地址
     */
    private String url;
    /**
     * 图片地址
     */
    private String thumb;
    /**
     * 文章标签：默认是1，1是普通文章，2是新的文章，3是热度文章
     */
    @TableField("label")
    private Integer label;
    /**
     * 点赞数
     */
    @TableField("likes")
    private Long likes;
    /**
     * 是否置顶
     */
    @TableField("top_status")
    private Integer topStatus;
    /**
     * 作者id
     */
    private Integer uid;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 创建时间
     */
    @TableField("input_time")
    private Long inputTime;
    /**
     * 更新人
     */
    private String updator;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Long updateTime;
    /**
     * 浏览数
     */
    private Long hits;
    /**
     * 分类id
     */
    private Integer tid;
    /**
     * 是否可以显示 0，不显示，1，显示
     */
    private Integer status;
    /**
     * 语言  EN 英文，CN中文
     */
    @TableField("type_language")
    private String typeLanguage;
    /**
     * 节点显示
     */
    @TableField("system_type")
    private String systemType;
    /**
     * 用户id
     */
    @TableField("uids")
    private String uidS;
    /**
     * 外部地址
     */
    @TableField("out_url")
    private String outUrl;





}
