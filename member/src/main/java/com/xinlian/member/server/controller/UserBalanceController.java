package com.xinlian.member.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.xinlian.member.biz.redis.RedisClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.CurrencyInfoRes;
import com.xinlian.common.request.GetOneTradeInfoReq;
import com.xinlian.common.request.TradeInfoReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.optionsconfig.ActivityConfig;
import com.xinlian.member.biz.service.TNewsArticleService;
import com.xinlian.member.biz.service.UserBalanceService;
import com.xinlian.member.server.vo.WalletTradeOrderVoConvertor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api("用户资产")
@RequestMapping("/{versionPath}/userBalance")
@RestController
@Slf4j
public class UserBalanceController {
	@Autowired
	private UserBalanceService userBalanceService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private TNewsArticleService tNewsArticleService;
	@Autowired
	private ActivityConfig activityConfig;
	@Autowired
	private RedisClient redisClient;
	@Value("${wallet.active.activePic}")
	private String activePic;
	@Value("${wallet.active.activeUrl}")
	private String activeUrl;

	/**
	 * 我的资产接口
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	@ApiOperation("获取用户所有资产列表")
	@PostMapping("/getAllCurrencyBalance")
	public ResponseResult getAllCurrencyBalance(HttpServletRequest httpServletRequest) {
		log.info("获取用户所有资产列表");
		Long userId = jwtUtil.getUserId(httpServletRequest);
		Long nodeId = jwtUtil.getNodeId(httpServletRequest);
		ResponseResult responseResult = userBalanceService.getAllCurrencyBalance(userId, nodeId);
		return responseResult;
	}

	/**
	 * 我的资产接口
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	@ApiOperation("获取用户所有资产列表和底部导航显示数据")
	@PostMapping("/getAllCurBalanceAndActivity")
	public ResponseResult getAllCurBalanceAndActivity(HttpServletRequest httpServletRequest) {
		log.info("获取用户所有资产列表和底部导航显示数据");
		Long userId = jwtUtil.getUserId(httpServletRequest);
		Long nodeId = jwtUtil.getNodeId(httpServletRequest);
		JSONObject jsonObject = new JSONObject();

		ResponseResult responseResult = userBalanceService.getAllCurrencyBalance(userId, nodeId);
		if (null != responseResult && null != responseResult.getResult()) {
			jsonObject = (JSONObject) responseResult.getResult();
		}

		ResponseResult responseResultActivity = tNewsArticleService.queryActivity(activityConfig);
		if (null != responseResultActivity && null != responseResultActivity.getResult()) {
			jsonObject.put("activityConfig", responseResultActivity.getResult());
		}
		//cms add 8.19 活动是否开启
		String flag = redisClient.get("BUY_GOODS_OPEN_FLAG");
		jsonObject.put("buyFlag", StringUtils.isBlank(flag) ? "close" : flag);

		//cms add 8.21 活动图片、跳转动态返回
		jsonObject.put("activePic", StringUtils.isBlank(flag) || StringUtils.equals("close", flag) ? "" : activePic);
		jsonObject.put("activeUrl", StringUtils.isBlank(flag) || StringUtils.equals("close", flag) ? "" : activeUrl);

		return ResponseResult.builder().result(jsonObject).code(ErrorCode.REQ_SUCCESS.getCode())
				.msg(ErrorCode.REQ_SUCCESS.getDes()).build();
	}

	/**
	 * 我的某个币种下接口
	 */
	@ApiOperation("我的某个币种下交易记录接口")
	@PostMapping("/getTradeInfo")
	public ResponseResult getTradeInfo(@RequestBody TradeInfoReq tradeInfoReq, HttpServletRequest httpServletRequest) {
		try {
			tradeInfoReq.setUid(jwtUtil.getUserId(httpServletRequest));
			PageInfo pageInfo = userBalanceService.getTradeInfo(tradeInfoReq);
			List convertList = new WalletTradeOrderVoConvertor().convertList(pageInfo.getList());
			return new ResponseResult(convertList);
		} catch (Exception e) {
			log.error("获取某个币种下列表数据异常:{}", e.toString(), e);
			return new ResponseResult(new BizException("网络异常!"));
		}
	}

	@ApiOperation(value = "钱包记录详情", notes = "tradeStatus->交易状态 申请-1;提交-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7")
	@PostMapping("/getOneTradeInfo")
	public ResponseResult getOneTradeInfo(@RequestBody GetOneTradeInfoReq getOneTradeInfoReq) {
		return userBalanceService.getOneTradeInfo(getOneTradeInfoReq);
	}

	@ApiOperation("查询对应币种资产详情币种余额")
	@PostMapping("/getCurrencyBalanceInfo")
	public ResponseResult getCurrencyBalanceInfo(@RequestBody CurrencyInfoRes currencyInfoRes,
			HttpServletRequest httpServletRequest) {
		currencyInfoRes.setUid(jwtUtil.getUserId(httpServletRequest));
		return userBalanceService.getCurrencyBalanceInfo(currencyInfoRes);
	}

}
