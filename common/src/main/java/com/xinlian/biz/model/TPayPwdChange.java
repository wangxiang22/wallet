package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.*;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用）
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@Data
@TableName("t_pay_pwd_change")
public class TPayPwdChange implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * uid
     */
    @TableField("uid")
    private Long uid;
    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;
    /**
     * 身份证后6位
     */
    @TableField("auth_no")
    private String authNo;
    /**
     * 手持身份证
     */
    @TableField("auth_scsfz")
    private String authScsfz;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 审核状态0审核中 1审核通过 2审核驳回
     */
    @TableField("state")
    private Integer state;
    /**
     * 支付密码
     */
    @TableField("pay_password")
    private String payPassword;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    @TableField(exist = false)
    private Long createTimeStamp;
    @TableField(exist = false)
    private Long updateTimeStamp;

    public Long getCreateTimeStamp() {
        if (this.createTime!=null) {
            return this.createTime.getTime();
        }
        return null;
    }

    public Long getUpdateTimeStamp() {
        if (this.updateTime!=null) {
            return this.updateTime.getTime();
        }
        return null;
    }

}
