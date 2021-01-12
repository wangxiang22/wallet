package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.enums.UserLevelStatusEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.WithdrawBudgetServiceFeeRequest;
import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.TUserAuthAppealService;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.LimitWithdrawHandler;
import com.xinlian.member.server.controller.handler.WalletInfoHandler;
import com.xinlian.member.server.controller.handler.WithdrawValidateHandler;
import com.xinlian.member.server.vo.response.wallet.CurrencyAddressResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * 资产钱包相关控制层
 */
@RestController
@RequestMapping("/{versionPath}/system")
@Api(value = "资产钱包相关接口")
@Slf4j
public class WalletInfoController {

    @Autowired
    private WalletInfoHandler walletInfoHandler;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private LimitWithdrawHandler limitWithdrawHandler;
    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;
    @Value("${swaggerAuth}")
    private String swaggerAuth;
    @Autowired
    private TUserAuthAppealService userAuthAppealService;
	@Autowired
	private WithdrawValidateHandler withdrawValidateHandler;
	@Autowired
	private HttpServletRequest request;

	/**
	 * 充币-提币回调接口
	 *
	 * @return
	 */
	@ApiOperation("充币-提币回调接口---11")
	@PassToken
	@PostMapping(value = "/v1/udun/callback", produces = "application/json; charset=utf-8")
	@UdunLogAnnotation(udunOpeType = "充币-提币回调接口")
	public String coinChargingCallBack(@RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
			@RequestParam("body") String body, @RequestParam("sign") String sign) {
		// U盾账户回调接口不接收
		return "error";
	}

	@ApiOperation(value = "提币接口")
	@PostMapping(value = "/v1/withdraw")
	@UdunLogAnnotation(udunOpeType = "系统发起提币接口")
	public ResponseResult withdrawCurrency(
			@ApiParam(name = "requestBody", value = WithdrawCurrencyRequest.PARAMS, required = true) @RequestBody WithdrawCurrencyRequest withdrawCurrencyRequest) {
		//set login token value to request
		withdrawCurrencyRequest.setUserId(jwtUtil.getUserId(request));
		withdrawCurrencyRequest.setServerNodeId(jwtUtil.getNodeId(request));
		//a distributed lock
		Lock lock = redisLockRegistry.obtain(jwtUtil.getToken(request));
		boolean redisLockFlag = true;
		try {
			//check withdraw resubmit
			if (!lock.tryLock()) {
				log.debug(Thread.currentThread().getName() + " : 请求提币接口，获取分布式锁失败!");
				redisLockFlag = false;
				throw new BizException("重复提交请稍后再试!");
			}
			//validate param
			withdrawValidateHandler.validateParameter(withdrawCurrencyRequest);
			log.info("请求参数：{}", JSONObject.toJSONString(withdrawCurrencyRequest));
			//withdraw check uid exist limit uid blacklist
			limitWithdrawHandler.doLimitWithdraw(withdrawCurrencyRequest.getCoin_id(),
					withdrawCurrencyRequest.getUserId());
			log.debug(Thread.currentThread().getName() + " : 请求提币接口，获取分布式锁成功!");
			String suffix = "CHECK_WITHDRAW_SMS" + jwtUtil.getToken(request);
			// 短信验证码24小时只验证一次
			// 验证paypassword
			TUserInfo userInfo = userInfoMapper.selectById(withdrawCurrencyRequest.getUserId());
			// 检验是否sms-code是否多少次请求
			checkSmsRuleHandler.doCheckSmsRuleHandler(
					SmsUtil.getCountryCodeAndPhone(userInfo.getMobile(), userInfo.getCountryCode()),
					SendRegisterTypeEnum.WITHDRAW_CURRENCY.getType() + "");
			// 验证客户激活状态
			if (UserLevelStatusEnum.FREEZE.getCode() == userInfo.getLevelStatus()) {
				throw new BizException("抱歉，该账户已被冻结!");
			}
			String phoneKey = "";
			if (!redisClient.exists(suffix)
					&& WithdrawCurrencyRequest.SMS.equals(withdrawCurrencyRequest.getWaitVerifyType())) {
				String phone = userInfo.getCountryCode() == 86 ? userInfo.getMobile()
						: userInfo.getCountryCode() + userInfo.getMobile();
				phoneKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(4, phone));
				String smsCode = redisClient.get(phoneKey);
				if (smsCode != null && withdrawCurrencyRequest.getSmsCode().equals(smsCode)) {
					// 验证一次删除
					checkSmsRuleHandler.doDeleteSmsRuleHandler(phone,
							SendRegisterTypeEnum.WITHDRAW_CURRENCY.getType() + "");
					// .set(suffix,smsCode,5);
				} else {
					checkSmsRuleHandler.doSaveSmsRuleHandler(phone,
							SendRegisterTypeEnum.WITHDRAW_CURRENCY.getType() + "");
					throw new BizException("输入短信验证码不正确或验证码已过期!");
				}
			} else if (!redisClient.exists(suffix)
					&& WithdrawCurrencyRequest.EMAIL.equals(withdrawCurrencyRequest.getWaitVerifyType())) {
				// 就需要
				String emailCode = redisClient.get(RedisConstant.EMAIL_CODE_KEY_PREFIX + userInfo.getEmail() + "_"
						+ MailTemplateEnum.OTHER_TYPE.getCode());
				if (emailCode == null || !withdrawCurrencyRequest.getSmsCode().equals(emailCode)) {
					throw new BizException("输入邮箱验证码不正确或邮箱验证码已过期!");
				}
			}
			if (!EncryptionUtil.checkMd5Pwd(userInfo.getPayPassWord(), userInfo.getSalt(),
					withdrawCurrencyRequest.getDeal_psw())) {
				throw new BizException("交易密码不正确!");
			}
			//check param finish execute withdraw
			walletInfoHandler.withdrawHandler(withdrawCurrencyRequest);
			//del sms redisKey
			if (StringUtils.isNotEmpty(phoneKey)) {
				redisClient.deleteByKey(phoneKey);
			}
		} catch (BizException e) {
			log.error("提币接口出现业务异常{}：{}", DateFormatUtil.get(7, new Date()), e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("提币接口出现系统异常{}：{}", DateFormatUtil.get(7, new Date()), e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		} finally {
			if (redisLockFlag) {
				lock.unlock();
				log.debug(Thread.currentThread().getName() + " : 请求提币接口，释放分布式锁success");
			}
			log.debug(Thread.currentThread().getName() + " : 请求提币接口，释放分布式锁失败!");
		}
		return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
	}

	@ApiOperation(value = "获取客户是否在24小时内输入过短信验证码")
	@GetMapping(value = "/v1/checkMobileSmsCode")
	public ResponseResult checkMobileSmsCode() {
		String suffix = "CHECK_WITHDRAW_SMS" + jwtUtil.getUserId(request);
		try {
			log.info("请求接口：获取客户是否在24小时内输入过短信验证码");
			String checkValue = redisClient.get(suffix);
			return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(null != checkValue)
					.build();
		} catch (BizException e) {
			log.error("获取客户是否在24小时内输入过短信验证码出现异常：{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("获取客户是否在24小时内输入过短信验证码出现异常：{}", e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		}
	}

	@ApiOperation(value = "提币接口 - 提币预计转账费用")
	@PostMapping(value = "/v1/budgetServiceFee")
	public ResponseResult budgetServiceFee(
			@ApiParam(name = "requestBody", value = WithdrawBudgetServiceFeeRequest.PARAMS, required = true) @RequestBody WithdrawBudgetServiceFeeRequest budgetServiceFeeRequest) {
		BigDecimal serviceFee = null;
		try {
			log.info("请求参数：{}", JSONObject.toJSONString(budgetServiceFeeRequest));
			serviceFee = walletInfoHandler.budgetServiceFeeRequest(budgetServiceFeeRequest);
		} catch (BizException e) {
			log.error("提币预计转账费用出现异常：{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error(" 提币预计转账费用出现异常：{}", e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		}
		return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(serviceFee).build();
	}

	@ApiOperation(value = "获取客户某个币种下的地址字符串")
	@PostMapping(value = "/v1/userCurrencyAddress")
	public ResponseResult getUserWalletInfoAddressByCurrencyId(
			@ApiParam(value = "币种id") @RequestParam("coin_id") String currencyId,
			@ApiParam(value = "userId-客户主键") @RequestParam("userId") String userId) {
		try {
		    //default get userId by token is login
            Long userIdL = jwtUtil.getUserId(request);
			log.info("请求参数：userId:{},coin_id:{}", userId, currencyId);
			CurrencyAddressResponse currencyAddressResponse = walletInfoHandler.getUserWalletInfoAddressByCurrencyId(userIdL, CurrencyEnum.USDT.getCurrencyId());
			log.info("获取客户某个币种下的地址字符串:{}",JSONObject.toJSONString(currencyAddressResponse));
			return new ResponseResult(currencyAddressResponse);
		} catch (BizException e) {
			log.error("获取客户某个币种下的地址字符串出现异常：{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("获取客户某个币种下的地址字符串出现异常：{}", e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		}
	}

	@ApiOperation(value = "获取客户钱包是否有地址", responseReference = "有地址：true,无地址：false")
	@PostMapping(value = "/v1/checkCurrencyAddressStatus")
	public ResponseResult checkCurrencyAddress() {
		Long userId = jwtUtil.getUserId(request);
		try {
			log.info("请求接口：获取客户钱包是否有地址");
			return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS)
					.result(walletInfoHandler.checkCurrencyAddressStatus(userId)).build();
		} catch (BizException e) {
			log.error("获取客户钱包是否有地址出现异常：{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("获取客户钱包是否有地址出现异常：{}", e.toString(), e);
			return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
		}
	}

    @ApiOperation(value = "maleChain-分配客户钱包地址")
    @GetMapping(value = "/v1/maleChain/currencyAddress")
    public ResponseResult maleChainCurrencyAddress() {
        Long userId = jwtUtil.getUserId(request);
        try {
            log.info("请求接口：获取客户钱包地址");
            if(walletInfoHandler.checkCurrencyAddressStatus(userId)){
                throw new BizException("已有存在钱包地址!");
            }
            if(userAuthAppealService.queryAuthStatusByUid(userId)){
                throw new BizException("请先实名再获取钱包地址!");
            }
            return new ResponseResult(walletInfoHandler.allocationCurrencyAddress(userId));
        }catch (BizException e){
            log.error("获取客户钱包是否有地址出现异常：{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error("获取客户钱包是否有地址出现异常：{}",e.toString(),e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "udun-分配客户钱包地址")
    @GetMapping(value = "/v1/allocationCurrencyAddress")
    public ResponseResult udunCurrencyAddress(HttpServletRequest request) {
        try {
            log.info("请求接口：获取客户钱包地址");
            if(true){
                throw new BizException("请升级到最新版本!");
            }
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result("").build();
        }catch (BizException e){
            return new ResponseResult(e);
        }
    }





}
