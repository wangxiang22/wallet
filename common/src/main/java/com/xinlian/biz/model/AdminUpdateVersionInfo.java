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
 * @since 2020-04-29
 */
@Data
@TableName("admin_update_version_info")
public class AdminUpdateVersionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * t_update_version表的主键id
     */
    private Integer vid;
    /**
     * 时间
     */
    private Long time;
    /**
     * 下发数量
     */
    @TableField("Issued")
    private Integer Issued;
    /**
     * 下载数量
     */
    private Integer download;

    /**
     * 1：安卓，2:ios
     */
    private Integer type;

}
