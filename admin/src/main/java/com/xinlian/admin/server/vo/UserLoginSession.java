package com.xinlian.admin.server.vo;

import com.xinlian.common.response.AdminUserRes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Description: 登录用户信息vo
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginSession {

	private AdminUserRes adminUserRes;

	private String md5Token;
	// 目录
	private String ticketMenu;
	// 角色
	private String ticketRole;
	// 按钮
	private String ticketLabel;
	// 角色可允许请求接口
	private String ticketInterfaceUrl;

	public UserLoginSession(AdminUserRes adminUserRes, String md5Token, String ticketMenu, String ticketRole, String ticketInterfaceUrl) {
		super();
		this.adminUserRes = adminUserRes;
		this.md5Token = md5Token;
		this.ticketMenu = ticketMenu;
		this.ticketRole = ticketRole;
		this.ticketInterfaceUrl = ticketInterfaceUrl;
	}

}
