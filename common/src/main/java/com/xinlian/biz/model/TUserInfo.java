package com.xinlian.biz.model;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinlian.common.response.AuthenticationRes;
import com.xinlian.common.response.UserCodeRes;
import com.xinlian.common.response.UserInfoDetailRes;
import com.xinlian.common.response.UserInfoRes;
import com.xinlian.common.utils.DateFormatUtil;

import lombok.Data;

@TableName("t_user_info")
@Data
public class TUserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * uid
	 */
	@TableId(value = "uid", type = IdType.AUTO)
	private Long uid;
	/**
	 * 用户名
	 */
	@TableField("user_name")
	private String userName;

	@TableField("user_name_new")
	private String userNameNew;
	/**
	 * 昵称
	 */
	@TableField("nick_name")
	private String nickName;
	/**
	 * 真实姓名
	 */
	@TableField("real_name")
	private String realName;
	/**
	 * 手机号码
	 */
	@TableField("mobile")
	private String mobile;
	/**
	 * 邮箱
	 */
	@TableField("email")
	private String email;
	/**
	 * 头像url
	 */
	@TableField("head_portrait_url")
	private String headPortraitUrl;
	/**
	 * 身份证号码
	 */
	@TableField("id_no")
	private String idNo;
	/**
	 * 身份证正面照片地址
	 */
	@TableField("auth_sfzfm_url")
	private String authSfzfmUrl;
	/**
	 * 身份证反面url地址
	 */
	@TableField("auth_sfzzm_url")
	private String authSfzzmUrl;
	/**
	 * 手持身份证照片url地址
	 */
	@TableField("auth_scsfz_url")
	private String authScsfzUrl;
	/**
	 * 实名认证状态 -1未认证 1已认证
	 */
	@TableField("real_auth_status")
	private Integer realAuthStatus;
	/**
	 * 注册ip
	 */
	@TableField("regip")
	private String regip;
	/**
	 * 注册时间戳
	 */
	@TableField("regtime")
	private String regtime;

	@TableField("reg_type")
	private Integer regType;

	/**
	 * 服务节点id
	 */
	@TableField("server_node_id")
	private Long serverNodeId;

	@TableField("server_node_name")
	private String serverNodeName;

	/**
	 * 是否更新密码 -1未更新 1更新登录密码 2更新支付密码
	 */
	@TableField("is_update_pass")
	private Integer isUpdatePass;
	/**
	 * 会员等级
	 */
	@TableField("levelid")
	private Integer levelid;
	/**
	 * 加密key
	 */
	@TableField("salt")
	private String salt;
	/**
	 * 登录密码
	 */
	@TableField("login_pass_word")
	private String loginPassWord;
	/**
	 * 支付密码
	 */
	@TableField("pay_pass_word")
	private String payPassWord;
	/**
	 * 最后登录时间
	 */
	@TableField("last_login_time")
	private Date lastLoginTime;
	/**
	 * token-登陆时效
	 */
	@TableField("token")
	private String token;
	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;

	@TableField("updateor")
	private Long updateor;

	@TableField("update_time")
	private Date updateTime;

	@TableField("invitation_code")
	private String invitationCode;

	@TableField("parent_id")
	private Long parentId;

	@TableField("parent_name")
	private String parentName;

	@TableField("level_status")
	private Integer levelStatus;

	@TableField("freeze_reson")
	private String freezeReson;

    /**
     * 给客户展示冻结原因字段
     */
    @TableField("show_freeze_reason")
    private String showFreezeReason;

    @TableField("country_code")
    private Integer countryCode;

	@TableField("orem_state")
	private Integer oremState;

	@TableField("active_time")
	private Date activeTime;

	@TableField("jid")
	private String jid;

	@TableField("type")
	private Integer type;

	@TableField("bind_count")
	private Integer bindCount;

	/**
	 * 省份名称
	 */
	@TableField("province_name")
	private String provinceName;
	/**
	 * 城市名称
	 */
	@TableField("city_name")
	private String cityName;
	/**
	 * 挖矿质押状态 - 1：未质押，2：待审核质押，3：已质押
	 */
	@TableField("pledge_state")
	private Integer pledgeState;
	/**
	 * 挖矿质押时间
	 */
	@TableField("pledge_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date pledgeTime;

	public UserInfoRes userInfoRes() {
		UserInfoRes res = new UserInfoRes();
		res.setCreateTime(DateFormatUtil.dateToLong(createTime));
		res.setEmail(email);
		res.setUid(uid);
		res.setUsername(userName);
		res.setCountryCode(countryCode);
		res.setInvitationCode(invitationCode);
		res.setLevelStatus(levelStatus);
		res.setAvatar(headPortraitUrl);
		res.setName(nickName);
		res.setRealName(realName);
		res.setPhone(mobile);
		res.setOremState(oremState);
		res.setActiveTime(DateFormatUtil.dateToLong(activeTime));
		res.setRealAuthStatus(realAuthStatus);
		res.setNodeName(serverNodeName);
		res.setParentName(parentName);
		res.setFreezeReson(freezeReson);
		res.setBindCount(bindCount);
		res.setUpdateTime(updateTime);
		res.setParentId(parentId);
		res.setProvinceName(provinceName);
		res.setCityName(cityName);
		res.setPledgeState(pledgeState);
		res.setPledgeTime(pledgeTime);
		return res;
	}

	public UserInfoDetailRes userInfoDetailRes() {
		UserInfoDetailRes res = new UserInfoDetailRes();
		res.setCreateTime(DateFormatUtil.dateToLong(createTime));
		res.setEmail(email);
		res.setUid(uid);
		res.setUsername(userName);
		res.setCountryCode(countryCode);
		res.setInvitationCode(invitationCode);
		res.setLevelStatus(levelStatus);
		res.setAvatar(headPortraitUrl);
		res.setName(nickName);
		res.setRealName(realName);
		res.setPhone(mobile);
		res.setOremState(oremState);
		res.setActiveTime(DateFormatUtil.dateToLong(activeTime));
		res.setRealAuthStatus(realAuthStatus);
		res.setNodeName(serverNodeName);
		res.setParentName(parentName);
		res.setIdNo(idNo);
		return res;
	}

	public UserCodeRes userCodeRes() {
		UserCodeRes res = new UserCodeRes();
		res.setInvitationCode(invitationCode);
		res.setUid(uid);
		return res;
	}

	public AuthenticationRes authenticationRes() {
		AuthenticationRes res = new AuthenticationRes();
		res.setCreateTime(DateFormatUtil.dateToLong(createTime));
		res.setEmail(email);
		res.setUid(uid);
		res.setUsername(userName);
		res.setCountryCode(countryCode);
		res.setInvitationCode(invitationCode);
		res.setLevelStatus(levelStatus);
		res.setAvatar(headPortraitUrl);
		res.setName(nickName);
		res.setPhone(mobile);
		res.setOremState(oremState);
		res.setActiveTime(DateFormatUtil.dateToLong(activeTime));
		return res;
	}
}
