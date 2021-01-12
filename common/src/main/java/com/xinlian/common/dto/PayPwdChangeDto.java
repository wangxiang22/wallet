package com.xinlian.common.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用）
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@Data
public class PayPwdChangeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * uid
     */
    private Long uid;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 身份证后6位
     */
    private String authNo;
    /**
     * 手持身份证
     */
    private String authScsfz;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 审核状态0审核中 1审核通过 2审核驳回
     */
    private Integer state;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 创建时间
     */
    private Long createTimeStamp;
    /**
     * 修改时间
     */
    private Long updateTimeStamp;
}
