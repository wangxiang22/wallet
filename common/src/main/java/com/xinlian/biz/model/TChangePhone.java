package com.xinlian.biz.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

@TableName("t_change_phone")
public class TChangePhone implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    /**
     * 国家代码
     */
    @TableField("country_code")
    private Integer countryCode;
    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;
    /**
     * 身份证号
     */
    @TableField("idcard_no")
    private String idcardNo;
    /**
     * 身份证反面
     */
    @TableField("idcard_img")
    private String idcardImg;
    /**
     * 身份证正面
     */
    @TableField("idcard_img1")
    private String idcardImg1;
    /**
     * 手持身份证
     */
    @TableField("idcard_man")
    private String idcardMan;
    /**
     * 处理人uid
     */
    @TableField("euid")
    private Long euid;
    /**
     * 处理人账号
     */
    @TableField("editor")
    private String editor;
    /**
     * 处理状态 0提交状态 1通过  2不通过
     */
    @TableField("status")
    private Integer status;
    /**
     * 处理备注
     */
    @TableField("remark")
    private String remark;
    /**
     * 申请时间
     */
    @TableField("create_time")
    private Long createTime;
    /**
     * 处理时间
     */
    @TableField("update_time")
    private Long updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Integer countryCode) {
        this.countryCode = countryCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdcardNo() {
        return idcardNo;
    }

    public void setIdcardNo(String idcardNo) {
        this.idcardNo = idcardNo;
    }

    public String getIdcardImg() {
        return idcardImg;
    }

    public void setIdcardImg(String idcardImg) {
        this.idcardImg = idcardImg;
    }

    public String getIdcardImg1() {
        return idcardImg1;
    }

    public void setIdcardImg1(String idcardImg1) {
        this.idcardImg1 = idcardImg1;
    }

    public String getIdcardMan() {
        return idcardMan;
    }

    public void setIdcardMan(String idcardMan) {
        this.idcardMan = idcardMan;
    }

    public Long getEuid() {
        return euid;
    }

    public void setEuid(Long euid) {
        this.euid = euid;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TChangePhone{" +
        ", id=" + id +
        ", uid=" + uid +
        ", phone=" + phone +
        ", countryCode=" + countryCode +
        ", realName=" + realName +
        ", idcardNo=" + idcardNo +
        ", idcardImg=" + idcardImg +
        ", idcardImg1=" + idcardImg1 +
        ", idcardMan=" + idcardMan +
        ", euid=" + euid +
        ", editor=" + editor +
        ", status=" + status +
        ", remark=" + remark +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
