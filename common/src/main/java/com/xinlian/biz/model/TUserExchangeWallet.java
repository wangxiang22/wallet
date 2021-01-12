package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("t_user_exchange_wallet")
public class TUserExchangeWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户钱包uid
     */
    @TableField("cat_uid")
    private Long catUid;
    /**
     * 用户交易所uid
     */
    @TableField("rocket_uid")
    private Long rocketUid;
    /**
     * 绑定状态1绑定2未绑定
     */
    private Integer status;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
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
     * 交易所身份证
     */
    @TableField("rocket_card")
    private String rocketCard;
    /**
     * 节点id
     */
    @TableField("system_type")
    private Long systemType;
    /**
     * 节点名称
     */
    @TableField("system_name")
    private String systemName;
    /**
     * 激活状态
     */
    private Integer active;

    @TableField("rocket_status")
    private Integer rocketStatus;
    @TableField("rocket_phone")
    private String rocketPhone;
    @TableField("country_code")
    private Integer countryCode;

    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private Long node;
    @TableField(exist = false)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCatUid() {
        return catUid;
    }

    public void setCatUid(Long catUid) {
        this.catUid = catUid;
    }

    public Long getRocketUid() {
        return rocketUid;
    }

    public void setRocketUid(Long rocketUid) {
        this.rocketUid = rocketUid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCatUsername() {
        return catUsername;
    }

    public void setCatUsername(String catUsername) {
        this.catUsername = catUsername;
    }

    public String getCatPhone() {
        return catPhone;
    }

    public void setCatPhone(String catPhone) {
        this.catPhone = catPhone;
    }

    public String getCatRealname() {
        return catRealname;
    }

    public void setCatRealname(String catRealname) {
        this.catRealname = catRealname;
    }

    public String getCatCardno() {
        return catCardno;
    }

    public void setCatCardno(String catCardno) {
        this.catCardno = catCardno;
    }

    public String getRocketUsername() {
        return rocketUsername;
    }

    public void setRocketUsername(String rocketUsername) {
        this.rocketUsername = rocketUsername;
    }

    public String getRocketEmail() {
        return rocketEmail;
    }

    public void setRocketEmail(String rocketEmail) {
        this.rocketEmail = rocketEmail;
    }

    public String getRocketRealname() {
        return rocketRealname;
    }

    public void setRocketRealname(String rocketRealname) {
        this.rocketRealname = rocketRealname;
    }

    public String getRocketCard() {
        return rocketCard;
    }

    public void setRocketCard(String rocketCard) {
        this.rocketCard = rocketCard;
    }

    public Long getSystemType() {
        return systemType;
    }

    public void setSystemType(Long systemType) {
        this.systemType = systemType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TUserExchangeWallet{" +
        ", id=" + id +
        ", catUid=" + catUid +
        ", rocketUid=" + rocketUid +
        ", status=" + status +
        ", createTime=" + createTime +
        ", catUsername=" + catUsername +
        ", catPhone=" + catPhone +
        ", catRealname=" + catRealname +
        ", catCardno=" + catCardno +
        ", rocketUsername=" + rocketUsername +
        ", rocketEmail=" + rocketEmail +
        ", rocketRealname=" + rocketRealname +
        ", rocketCard=" + rocketCard +
        ", systemType=" + systemType +
        ", systemName=" + systemName +
        ", active=" + active +
        "}";
    }
}
