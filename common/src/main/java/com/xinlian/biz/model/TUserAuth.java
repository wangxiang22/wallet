package com.xinlian.biz.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import lombok.Data;

@TableName("t_user_auth")
@Data
public class TUserAuth implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 用户id
	 */
	private Long uid;
	/**
	 * 用户账号
	 */
	private String username;
	/**
	 * 身份证号
	 */
	@TableField("auth_sn")
	private String authSn;
	/**
	 * 申请ip
	 */
	@TableField("input_ip")
	private String inputIp;
	/**
	 * 申请时间
	 */
	@TableField("input_time")
	private Long inputTime;
	/**
	 * 处理人uid
	 */
	private Long euid;
	/**
	 * 处理人账号
	 */
	private String editor;
	/**
	 * 处理备注
	 */
	private String note;
	/**
	 * 处理状态 1待审核  2拒绝  3通过
	 */
	private Integer status;
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
	 * 实名认证提交次数
	 */
	private Integer count;
	/**
	 * 1:中国 2：外国
	 */
	private Integer from;
	/**
	 * 真实姓名
	 */
	@TableField("real_name")
	private String realName;
	/**
	 * 处理时间
	 */
	@TableField("edit_time")
	private Long editTime;

	@TableField("node")
	private Long node;
	@TableField(exist = false)
	private String mobile;
	// 矿机激活状态 0未激活1已激活
	@TableField(exist = false, value = "orem_state")
	private Integer oremState;
	@TableField(exist = false)
	private Long startTime;
	@TableField(exist = false)
	private Long endTime;

	public TUserAuth(Long uid) {
		super();
		this.uid = uid;
	}

	public TUserAuth() {
		super();
	}

}
