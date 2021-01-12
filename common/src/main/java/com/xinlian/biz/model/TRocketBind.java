package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lx
 * @since 2020-03-23
 */
@Data
@TableName("t_rocket_bind")
public class TRocketBind implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 钱包uid
     */
    @TableField("cat_uid")
    private Long catUid;
    /**
     * 交易所uid
     */
    @TableField("rocket_uid")
    private Long rocketUid;
    /**
     * 1、绑定2解绑
     */
    private Integer state;
    /**
     * 激活状态 0未激活1激活
     */
    private Integer active;
    /**
     * 钱包用户名
     */
    @TableField("cat_username")
    private String catUsername;
    /**
     * 钱包手机号
     */
    @TableField("cat_phone")
    private String catPhone;
    /**
     * 钱包真实姓名
     */
    @TableField("cat_realname")
    private String catRealname;
    /**
     * 钱包身份证号
     */
    @TableField("cat_cardno")
    private String catCardno;
    /**
     * 交易所用户名
     */

    @TableField("rocket_username")
    private String rocketUsername;
    /**
     * 交易所邮箱
     */
    @TableField("rocket_email")
    private String rocketEmail;
    /**
     * 交易所实名
     */
    @TableField("rocket_realname")
    private String rocketRealname;
    /**
     * 节点id
     */
    @TableField("system_type")
    private String systemType;
    /**
     * 节点名称
     */
    @TableField("system_name")
    private String systemName;
    /**
     * 0-未认证 1待审核 2-审核不通过 3-已认证
     */
    @TableField("rocket_status")
    private Integer rocketStatus;
    /**
     * 交易所手机号
     */
    @TableField("rocket_phone")
    private String rocketPhone;
    /**
     * 区号
     */
    @TableField("country_code")
    private Integer countryCode;
    /**
     * 交易所身份证
     */
    @TableField("rocket_card")
    private String rocketCard;


    @TableField(exist = false)
    private Long createTimeStamp;

    public Long getCreateTimeStamp() {
        if (this.createTime != null) {
            return this.createTime.getTime();
        }
        return null;
    }


}
