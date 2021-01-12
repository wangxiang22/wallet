package com.xinlian.biz.model.hicat;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  HiCat-用户表
 * </p>
 *
 * @author lt
 * @since 2020-07-15
 */
@Data
@TableName("t_user")
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField("_uid")
    private String uid;
    @TableField("_name")
    private String name;
    @TableField("_display_name")
    private String displayName;
    @TableField("_gender")
    private Integer gender;
    @TableField("_portrait")
    private String portrait;
    @TableField("_mobile")
    private String mobile;
    @TableField("_email")
    private String email;
    @TableField("_address")
    private String address;
    @TableField("_company")
    private String company;
    @TableField("_social")
    private String social;
    @TableField("_passwd_md5")
    private String passwdMd5;
    @TableField("_salt")
    private String salt;
    @TableField("_extra")
    private String extra;
    @TableField("_type")
    private Integer type;
    @TableField("_dt")
    private Long dt;
    @TableField("_createTime")
    private Date createtime;
    @TableField("_deleted")
    private Integer deleted;


}
