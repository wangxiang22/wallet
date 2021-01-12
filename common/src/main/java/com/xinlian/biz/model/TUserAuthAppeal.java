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
 * 实名申诉表
 * </p>
 *
 * @since 2020-03-27
 */
@Data
@TableName("t_user_auth_appeal")
public class TUserAuthAppeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;
    /**
     * 节点id
     */
    @TableField("node_id")
    private Long nodeId;
    /**
     * 节点名称
     */
    @TableField("node_name")
    private String nodeName;
    /**
     * 原真实姓名
     */
    @TableField("original_real_name")
    private String originalRealName;
    /**
     * 申诉姓名
     */
    @TableField("appeal_real_name")
    private String appealRealName;
    /**
     * 身份证反面
     */
    @TableField("auth_sfzfm")
    private String authSfzfm;
    /**
     * 身份证正面
     */
    @TableField("auth_sfzzm")
    private String authSfzzm;
    /**
     * 手持身份证
     */
    @TableField("auth_scsfz")
    private String authScsfz;
    /**
     * 申诉审核状态 - 1：待审核，2：已拒绝，3：已更正
     */
    @TableField("appeal_status")
    private Integer appealStatus;
    /**
     * 处理原因
     */
    private String note;
    /**
     * 提交申诉次数
     */
    @TableField("appeal_count")
    private Integer appealCount;
    /**
     * 创建时间（申请时间）
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
