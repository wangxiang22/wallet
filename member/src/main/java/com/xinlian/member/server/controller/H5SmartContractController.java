package com.xinlian.member.server.controller;

import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.CurrencyInfoRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.UserBalanceService;
import com.xinlian.member.server.controller.handler.WalletInfoHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Song
 * @date 2020-07-16 11:38
 * @description
 */
@Api("留个智能合约h5接口")
@RestController
@Slf4j
public class H5SmartContractController {

	@Autowired
	private UserBalanceService userBalanceService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private WalletInfoHandler walletInfoHandler;
	@Autowired
	private HttpServletRequest request;

	@ApiOperation("查询对应币种资产详情币种余额")
	@PostMapping("/userBalance/getCurrencyBalanceInfo")
	public ResponseResult getCurrencyBalanceInfo(@RequestBody CurrencyInfoRes currencyInfoRes,
			HttpServletRequest httpServletRequest) {
		currencyInfoRes.setUid(jwtUtil.getUserId(httpServletRequest));
		return userBalanceService.getCurrencyBalanceInfo(currencyInfoRes);
	}

	@ApiOperation(value = "获取客户某个币种下的地址字符串")
	@PostMapping(value = "/system/v1/userCurrencyAddress")
	public ResponseResult getUserWalletInfoAddressByCurrencyId(
			@ApiParam(value = "币种id") @RequestParam("coin_id") String currencyId,
			@ApiParam(value = "userId-客户主键") @RequestParam("userId") String userId) {
		String currencyAddress = null;
		try {
			log.info("请求参数：userId:{},coin_id:{}", userId, currencyId);
            Long userIdL = jwtUtil.getUserId(request);
			currencyAddress = walletInfoHandler.getUserWalletInfoAddressByCurrencyId(userIdL, CurrencyEnum.USDT.getCurrencyId()).getBasicAddress();
			return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(currencyAddress)
					.build();
		} catch (BizException e) {
			log.error("获取客户某个币种下的地址字符串出现异常：{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("获取客户某个币种下的地址字符串出现异常：{}", e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		}
	}
}
