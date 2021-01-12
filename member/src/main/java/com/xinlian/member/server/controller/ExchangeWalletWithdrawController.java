package com.xinlian.member.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinlian.biz.model.UserCurrencyStateReq;
import com.xinlian.common.request.ExchangeBalanceReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.ExchangeWalletWithdrawService;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.TWalletTradeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 流程：<br>
 * 1.判断币种是否可以重提<br>
 * 2.判断用户当日是否还有 冲提额度，冲提次数 <br>
 * 3.判断用户该笔冲提是否超过单笔限额<br>
 * 4.用户冲提币需要 验证码<br>
 * 5.校验验证码完成后可进行冲提操作<br>
 * 6.请求交易所 <br>
 * 7.收到结果后判断<br>
 * 8.如成功请求 对用户钱包账户数据库增加/减少某币种数量 <br>
 * 9.新增一条<br>
 * 10.成功返回
 */
@Api("钱包交易所冲提")
@RequestMapping("/{versionPath}exchangewalletwithdraw")
@RestController
public class ExchangeWalletWithdrawController {
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private ExchangeWalletWithdrawService exchangeWalletWithdrawService;
	@Autowired
	private IRegisterLoginService registerLoginService;
	@Autowired
	private TWalletTradeOrderService tWalletTradeOrderService;
	@Autowired
	private HttpServletRequest httpServletRequest;

	@ApiOperation("获取用户交易所余额")
	@PostMapping("getExchangeBalance")
	public ResponseResult getExchangeBalance(@RequestBody ExchangeBalanceReq exchangeBalanceReq) {
		return exchangeWalletWithdrawService.getExchangeBalance(exchangeBalanceReq);
	}

	@ApiOperation("冲提")
	@PostMapping("withdraw")
	public ResponseResult withdraw(@RequestBody UserCurrencyStateReq userCurrencyStateReq) {
		userCurrencyStateReq.setUid(jwtUtil.getUserId(httpServletRequest));
		return exchangeWalletWithdrawService.withdraw(userCurrencyStateReq);
	}

	@ApiOperation("查询用户币种状态是否可以冲提")
	@PostMapping("getUserCurrencyState")
	public ResponseResult getUserCurrencyState(@RequestBody UserCurrencyStateReq userCurrencyStateReq) {
		userCurrencyStateReq.setUid(jwtUtil.getUserId(httpServletRequest));
		return exchangeWalletWithdrawService.getUserCurrencyState(userCurrencyStateReq);
	}

	@ApiOperation("获取交易所钱包冲提验证码")
	@PostMapping("getCode")
	public ResponseResult getCode(@RequestBody RegisterReq registerReq) {
		registerReq.setType(11);
		if (null == registerReq.getNodeId()) {
			registerReq.setNodeId(jwtUtil.getNodeId(httpServletRequest));
		}
		return registerLoginService.sendRegisterSms(registerReq, false);
	}

	@ApiOperation("当前币种是否可冲提及余额")
	@PostMapping("getCurrencyBalance")
	public ResponseResult getCurrencyBalance(@RequestBody UserCurrencyStateReq userCurrencyStateReq) {
		userCurrencyStateReq.setUid(jwtUtil.getUserId(httpServletRequest));
		return exchangeWalletWithdrawService.getCurrencyBalance(userCurrencyStateReq);
	}

	@ApiOperation("历史充提记录")
	@PostMapping("withdrawHistory")
	public ResponseResult withdrawHistory(HttpServletRequest httpServletRequest) {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		return exchangeWalletWithdrawService.withdrawHistory(userId);
	}
}
