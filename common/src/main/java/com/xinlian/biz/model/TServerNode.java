package com.xinlian.biz.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinlian.common.response.NodeDicRes;
import com.xinlian.common.response.NodeRes;
import com.xinlian.common.response.ServerNodeRes;
import com.xinlian.common.utils.DateFormatUtil;

import lombok.Data;

@Data
@TableName("t_server_node")
public class TServerNode implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;
	/**
	 * 节点上级id
	 */
	@TableField("parent_id")
	private Long parentId;
	/**
	 * 节点所有上级id
	 */
	@TableField("parent_ids")
	private String parentIds;
	/**
	 * 节点名称
	 */
	@TableField("name")
	private String name;
	/**
	 * 节点别名
	 */
	@TableField("nickname")
	private String nickname;
	/**
	 * 节点logo图片地址
	 */
	@TableField("logo_url")
	private String logoUrl;
	/**
	 * 是否有下级 - 0：没有下级，1：有下级
	 */
	@TableField("child_status")
	private Integer childStatus;
	/**
	 * 是否前端隐藏（该字段暂时不用） - 0：前端隐藏，1：前端显示
	 */
	@TableField("hidden_status")
	private Integer hiddenStatus;
	/**
	 * 节点下级所有id
	 */
	@TableField("child_ids")
	private String childIds;
	/**
	 * 排列顺序
	 */
	@TableField("display_order")
	private Integer displayOrder;
	/**
	 * 激活矿机需要数量（USDT）
	 */
	@TableField("active_require_money")
	private BigDecimal activeRequireMoney;
	/**
	 * 是否可以注册 - 0：不可以注册，1：可以注册
	 */
	@TableField("register_status")
	private Integer registerStatus;
	/**
	 * 是否可以登录 - 0：不可以登录，1：可以登录
	 */
	@TableField("login_status")
	private Integer loginStatus;
	/**
	 * 是否可以激活 - 0：不可以激活，1：可以激活
	 */
	@TableField("active_status")
	private Integer activeStatus;
	/**
	 * 是否可以算能质押 - 0：不可以质押，1：可以质押
	 */
	@TableField("pledge_status")
	private Integer pledgeStatus;
	/**
	 * 是否可以智能合约 - 0：不可以智能合约，1：可以智能合约
	 */
	@TableField("smart_contracts_status")
	private Integer smartContractsStatus;
	/**
	 * 是否可以转换节点 - 0：不可以转换，1：可以转换
	 */
	@TableField("change_node_status")
	private Integer changeNodeStatus;
	/**
	 * 是否可以实名认证 - 0：不可以认证，1：可以认证
	 */
	@TableField("auth_status")
	private Integer authStatus;
	/**
	 * 是否可以邀请好友 - 0：不可以邀请，1：可以邀请
	 */
	@TableField("invite_status")
	private Integer inviteStatus;
	/**
	 * 是否可以站外提现 - 0：不可以提现，1：可以提现
	 */
	@TableField("withdraw_status")
	private Integer withdrawStatus;
	/**
	 * 是否可以跨节点邀请好友 - 0：不可以跨节点邀请，1：可以跨节点邀请
	 */
	@TableField("different_node_invite")
	private Integer differentNodeInvite;
	/**
	 * 手机号在当前节点绑定注册的个数限制 - 0：不超过5个，非0：不超过填写的数字。当节点属于大航海计划的，则此字段含义为当前节点注册的未激活账号数量限制。
	 */
	@TableField("mobile_register_amount")
	private Integer mobileRegisterAmount;
	/**
	 * 身份证号在当前节点绑定注册的个数限制 - 0：不做限制，非0：不超过填写的数字
	 */
	@TableField("auth_register_amount")
	private Integer authRegisterAmount;
	/**
	 * 邮箱在当前节点绑定的个数限制 - 0：不做限制，非0：不超过填写的数字。当节点属于大航海计划的，则此字段含义为当前节点绑定的未激活账号数量限制。
	 */
	@TableField("email_bind_amount")
	private Integer emailBindAmount;
	/**
	 * 是否可以转账CAT - 1：全部开启，2：只支持同节点，3：只支持不同节点，4：全部不开启
	 */
	@TableField("transfer_CAT_status")
	private Integer transferCatStatus;
	/**
	 * 是否可以转账USDT - 1：全部开启，2：只支持同节点，3：只支持不同节点，4：全部不开启
	 */
	@TableField("transfer_USDT_status")
	private Integer transferUsdtStatus;
	/**
	 * 是否可以转账CAG - 1：全部开启，2：只支持同节点，3：只支持不同节点，4：全部不开启
	 */
	@TableField("transfer_CAG_status")
	private Integer transferCagStatus;
	/**
	 * 是否可以转账GPT - 1：全部开启，2：只支持同节点，3：只支持不同节点，4：全部不开启
	 */
	@TableField("transfer_GPT_status")
	private Integer transferGptStatus;
	/**
	 * 是否可以充值CAT - 0：不可以充值，1：可以充值
	 */
	@TableField("recharge_CAT_status")
	private Integer rechargeCatStatus;
	/**
	 * 是否可以充值USDT - 0：不可以充值，1：可以充值
	 */
	@TableField("recharge_USDT_status")
	private Integer rechargeUsdtStatus;
	/**
	 * 是否可以充值CAG - 0：不可以充值，1：可以充值
	 */
	@TableField("recharge_CAG_status")
	private Integer rechargeCagStatus;
	/**
	 * 是否可以充值GPT - 0：不可以充值，1：可以充值
	 */
	@TableField("recharge_GPT_status")
	private Integer rechargeGptStatus;
	/**
	 * 是否可以提现CAT - 0：不可以提现，1：可以提现
	 */
	@TableField("cash_CAT_status")
	private Integer cashCatStatus;
	/**
	 * 是否可以提现USDT - 0：不可以提现，1：可以提现
	 */
	@TableField("cash_USDT_status")
	private Integer cashUsdtStatus;
	/**
	 * 是否可以提现CAG - 0：不可以提现，1：可以提现
	 */
	@TableField("cash_CAG_status")
	private Integer cashCagStatus;
	/**
	 * 是否可以提现GPT - 0:不可以体现，1：可以体现
	 */
	@TableField("cash_GPT_status")
	private Integer cashGptStatus;
	/**
	 * 是否可以绑定火箭交易所 - 0：不可以绑定，1：可以绑定
	 */
	@TableField("bind_rocket_status")
	private Integer bindRocketStatus;
	/**
	 * 创建时间
	 */
	@TableField("create_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@TableField("update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	public ServerNodeRes serverNodeRes() {
		ServerNodeRes nodeRes = new ServerNodeRes();
		nodeRes.setId(id);
		nodeRes.setParentId(parentId);
		nodeRes.setParentIds(parentIds);
		nodeRes.setName(name);
		nodeRes.setActiveRequireMoney(activeRequireMoney);
		nodeRes.setLogoUrl(logoUrl);
		nodeRes.setRegisterStatus(registerStatus);
		nodeRes.setLoginStatus(loginStatus);
		nodeRes.setBindRocketStatus(bindRocketStatus);
		nodeRes.setDisplayOrder(displayOrder);
		nodeRes.setCreateTime(DateFormatUtil.formatTillSecond(createTime));
		nodeRes.setUpdateTime(DateFormatUtil.formatTillSecond(updateTime));
		return nodeRes;
	}

	public NodeRes nodeRes() {
		NodeRes nodeRes = new NodeRes();
		nodeRes.setId(id);
		nodeRes.setName(name);
		nodeRes.setActiveRequireMoney(activeRequireMoney);
		nodeRes.setLogoUrl(logoUrl);
		nodeRes.setBindRocketStatus(bindRocketStatus);
		return nodeRes;
	}

	public NodeDicRes nodeDicRes() {
		NodeDicRes res = new NodeDicRes();
		res.setName(name);
		res.setNodeId(id);
		res.setParentId(parentId);
		return res;
	}

}
