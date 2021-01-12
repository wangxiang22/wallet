package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息内容表
 * </p>
 *
 * @author lt
 * @since 2020-06-13
 */
@Data
@TableName("admin_message_content")
public class AdminMessageContent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id（消息唯一标识）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 标题
     */
    @TableField("title")
    private String title;
    /**
     * 内容
     */
    @TableField("content")
    private String content;
    /**
     * 消息类型 - 1：账户消息，2：活动消息，3：系统消息
     */
    @TableField("type_code")
    private Integer typeCode;
    /**
     * 缩略图地址
     */
    @TableField("thumbnail")
    private String thumbnail;
    /**
     * 活动消息详情链接
     */
    @TableField("hyperlink")
    private String hyperlink;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;


}
