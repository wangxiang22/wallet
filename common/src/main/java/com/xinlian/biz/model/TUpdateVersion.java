package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author WX
 * @since 2020-04-27
 */
@Data
@TableName("t_update_version")
public class TUpdateVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 推送安卓还是ios,1是安卓，2是ios
     */
    private Integer type;
    /**
     * 更新的版本号
     */
    private String version;
    /**
     * 新包链接地址
     */
    private String url;
    /**
     * 开始时间
     */
    @TableField("startTime")
    private Long startTime;
    /**
     * 结束时间
     */
    @TableField("endTime")
    private Long endTime;
    /**
     * 是否强制更新，0：否，1：是
     */
    @TableField("forceUpdate")
    private Integer forceUpdate;
    /**
     * 是否开启
     */
    private Integer status;
    /**
     * 更新内容
     */
    private String content;
    /**
     * 推送新包的图片地址
     */
    @TableField("thumb")
    private String thumb;


}
