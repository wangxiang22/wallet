package com.xinlian.member.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.QueryEmailRequest;
import com.xinlian.common.response.QueryEmailResponse;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.UserInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * com.xinlian.member.server.controller
 *
 * @date 2020/2/13 10:10
 */
@Slf4j
@RestController
@RequestMapping("/{versionPath}/system")
@Api(value = "便利查询控制器")
public class ConvenienceQueryController {

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * 根据节点id,节点客户登录用户名获取对应注册邮箱
	 *
	 * @return
	 */
	@ApiOperation("根据节点id,节点客户登录用户名获取对应注册邮箱")
	@PassToken
	@PostMapping(value = "/v1/convenience/queryEmail")
	public ResponseResult queryEmail(
			@ApiParam(name = "queryEmailRequest", value = QueryEmailRequest.PARAMS, required = true) @RequestBody QueryEmailRequest queryEmailRequest) {
		try {
			if (null == queryEmailRequest.getServerNodeId()) {
				queryEmailRequest.setServerNodeId(jwtUtil.getNodeId(request));
			}

			// 验签
			String getSign = DigestUtils
					.md5Hex(queryEmailRequest.getLoginUserName() + queryEmailRequest.getServerNodeId());
			if (!queryEmailRequest.getSign().equals(getSign)) {
				throw new BizException("验签失败，请核实!");
			}
			TUserInfo userInfo = new TUserInfo();
			userInfo.setUserName(queryEmailRequest.getLoginUserName());
			userInfo.setServerNodeId(queryEmailRequest.getServerNodeId());
			TUserInfo getUserInfo = userInfoService.getOneModel(userInfo);
			if (null == getUserInfo) {
				throw new BizException("无对应信息");
			}
			// 存放到redis中
			redisClient.set(getSign, getUserInfo, 10 * 60);
			QueryEmailResponse emailResponse = convertToQueryEmailResponse(getUserInfo);
			return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(emailResponse)
					.build();
		} catch (BizException e) {
			log.error("节点客户登录用户名获取对应注册邮箱业务异常:{}", e.toString(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("节点客户登录用户名获取对应注册邮箱:{}", e.toString(), e);
			return new ResponseResult(new BizException("出现异常"));
		}
	}

	private QueryEmailResponse convertToQueryEmailResponse(TUserInfo getUserInfo) {
		QueryEmailResponse emailResponse = new QueryEmailResponse();
		if (null != getUserInfo.getEmail()) {
			emailResponse.setEmail(getUserInfo.getEmail().trim());
		}
		if (null != getUserInfo.getMobile()) {
			emailResponse.setPhone(getUserInfo.getMobile().trim());
		}
		emailResponse.setServerNodeId(getUserInfo.getServerNodeId());
		emailResponse.setUserName(getUserInfo.getUserName());
		emailResponse.setCountryCode(getUserInfo.getCountryCode());
		return emailResponse;
	}

}
