package com.xinlian.biz.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.xinlian.common.response.AdminUserRes;
import com.xinlian.common.utils.DateFormatUtil;

import lombok.Data;

@Data
@TableName("admin_user")
public class AdminUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 用户名
	 */
	@TableField("username")
	private String username;
	/**
	 * 真实姓名
	 */
	@TableField("real_name")
	private String realName;
	/**
	 * 登录密码
	 */
	@TableField("password")
	private String password;
	/**
	 * 加密
	 */
	@TableField("salt")
	private String salt;
	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;
	/**
	 * 创建人
	 */
	@TableField("creater")
	private Long creater;
	/**
	 * 修改时间
	 */
	@TableField("update_time")
	private Date updateTime;
	/**
	 * 修改人
	 */
	@TableField("updater")
	private Long updater;
	/**
	 * 用户状态 0禁用 1启用
	 */
	@TableField("status")
	private Integer status;

	/**
	 * 账户描述 -
	 */
	@TableField("account_desc")
	private String accountDesc;

	/**
	 * 所属权限组的名称
	 */
	@TableField(exist = false)
	private String roleNameByUser;

	@TableField("email_address")
	private String emailAddress;

	@Override
	public String toString() {
		return "AdminUser{" + ", id=" + id + ", username=" + username + ", realName=" + realName + ", password="
				+ password + ", salt=" + salt + ", createTime=" + createTime + ", creater=" + creater + ", updateTime="
				+ updateTime + ", updater=" + updater + ", status=" + status + ", accountDesc=" + accountDesc + "}";
	}

	public AdminUserRes adminUserRes() {
		AdminUserRes res = new AdminUserRes();
		res.setCreateTime(DateFormatUtil.dateToLong(createTime));
		res.setRealName(realName);
		res.setUsername(username);
		res.setUserId(id);
		return res;
	}

	public AdminUser(String username) {
		super();
		this.username = username;
	}

	public AdminUser() {
		super();
	}

}
