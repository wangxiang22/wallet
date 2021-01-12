package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 质押成功请求算力地球接口记录表
 * </p>
 *
 * @author lt
 * @since 2020-06-28
 */
@Data
@TableName("t_pledge_mining_log")
public class TPledgeMiningLog implements Serializable,Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 请求算力地球接口推送状态 - 1：推送成功，2：推送失败，3：待再次推送（此数值手动修改）
     */
    @TableField("status")
    private Integer status;
    /**
     * 算力地球接口相应结果
     */
    @TableField("result")
    private String result;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 请求算力地球接口的时间
     */
    @TableField("request_time")
    private Date requestTime;


    @Override
    public TPledgeMiningLog clone() throws CloneNotSupportedException {
        return (TPledgeMiningLog) super.clone();
    }
}
