package com.xinlian.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinlian.biz.model.TServerNode;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserInfoRes {
	private String token;
	private Long uid;
	private String email;
	private String username;
	// 创建时间
	private Long createTime;
	// 地区码
	private Integer countryCode;
	// 地区码对应name
	private String countryZhName;
	private String countryEnName;
	// 0冻结 1普通用户 2会员
	private Integer levelStatus;
	// 邀请码
	private String invitationCode;
	// 实名认证状态 0未认证 1已认证
	private Integer realAuthStatus;
	private String phone;
	private String name;
	private String realName;
	private String avatar;
	// 矿机激活状态 0未激活1已激活
	private Integer oremState;
	// 矿机激活时间
	private Long activeTime;
	// 邀请人
	private String parentName;
	// 节点名称
	private String nodeName;
	// 身份证号
	private String idNo;
	// 冻结原因
	private String freezeReson;
	// 0不是链权人 1是链权人
	private int chainOwner;
	//
	private Long exchangeId;
	// 绑定交易所次数
	private Integer bindCount;
	private Date updateTime;

	private Long parentId;
	private String parentPhone;
	private Long parentNodeId;
	private String parentNodeName;
	private List<InviteUserRes> byInviteUsers;
	private InviteUserRes inviteUserRes;
	// 省份名称
	private String provinceName;
	// 城市名称
	private String cityName;
	// 算力地球链接地址
	private String calEarthQrUrl;
	// 挖矿质押状态 - 1：未质押，2：待审核质押，3：已质押
	private Integer pledgeState;
	// 挖矿质押时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date pledgeTime;
	/**
	 * 实名认证最小年龄限制
	 */
	private int authMinAge;
	/**
	 * 实名认证最大年龄限制
	 */
	private int authMaxAge;

	/**
	 * 节点信息
	 */
	private TServerNode tServerNode;
	/**
	 * 最顶级节点
	 */
	private Long topNodeId;

}
