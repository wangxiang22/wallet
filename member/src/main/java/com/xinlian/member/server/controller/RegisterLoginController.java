package com.xinlian.member.server.controller;


import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.request.*;
import com.xinlian.common.response.*;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.jwt.annotate.EncryptionAnnotation;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.*;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.LimitWithdrawHandler;
import com.xinlian.member.server.controller.handler.RegisterHandler;
import com.xinlian.member.server.vo.AdProofDiagramVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Api(value = "注册登录")
@Controller
@RequestMapping("/{versionPath}/register")
@Slf4j
public class RegisterLoginController {

	@Autowired
	private IRegisterLoginService registerLoginService;
	@Autowired
	private AliyunEmailService aliyunEmailService;
	@Autowired
	private RedisClient redisClient;
	@Autowired
	private MailBindService mailBindService;
	@Autowired
	private IServerNodeService serverNodeService;
	@Autowired
	private AdProofDiagramService adProofDiagramService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private RedisLockRegistry redisLockRegistry;
	@Autowired
	private NodeVoyageUtil nodeVoyageUtil;
	@Autowired
	private RegisterHandler registerHandler;
	@Autowired
	private LimitWithdrawHandler limitWithdrawHandler;
	@Autowired
	private CheckSmsRuleHandler checkSmsRuleHandler;
	@Autowired
	private AdminOptionsUtil adminOptionsUtil;
	@Autowired
	private HttpServletRequest request;

    @ApiOperation(value = "发送注册短信-校验是否可注册等逻辑", httpMethod = "POST")
    @RequestMapping(value = "/sms/send", method = RequestMethod.POST)
    @ResponseBody
    @PassToken
    @CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    public ResponseResult sendRegisterSms(@RequestBody Map<String,  String> paramMap){
        RegisterReq req = registerHandler.decodeDataToObject(paramMap);
        boolean lockFlag = true;
        Long loginUidBelongNodeId = jwtUtil.getNodeIdCompatException(request,-1L);
        if(-1!=loginUidBelongNodeId){//登录状态下
        	req.setNodeId(jwtUtil.getNodeId(request));
		}
        Lock lock = redisLockRegistry.obtain(req.getPhone()+req.getNodeId());
        //检验这个手机号码是否具有资格访问这个接口
        checkSmsRuleHandler.doCheckSmsRuleHandler(SmsUtil.getCountryCodeAndPhone(req.getPhone(),req.getCountryCode()),req.getType()+"");
        try {
            if(!lock.tryLock()){
                lockFlag = false;
                throw new BizException("短信发送中,请耐心等待!");
            }
            //验证节点是否在限制集合里面
            limitWithdrawHandler.doLimitServerNodeId(req);
            boolean isInland = true;
            if(nodeVoyageUtil.belongVoyageNode(req.getNodeId())){
                if(86==req.getCountryCode()){
                    throw new BizException("请确定国家区号是否正确!");
                }
                String isCheckInlandFlag = redisClient.get(RedisConstant.APP_REDIS_PREFIX + "IS_CHECK_INLAND_FLAG");
                //验证手机号
                if(null!=isCheckInlandFlag && req.getPhone().length()==11 && "1".equals(req.getPhone().substring(0,1))){
                    throw new BizException("请确认手机号码!");
                }
                //航海计划节点 -注册type 才去判断
                if(req.getType()<=1 || req.getType()==813){
                    registerHandler.judgeAbroadNodeIsRegister(req);
                }
                isInland = false;
            }
            return registerLoginService.sendRegisterSms(req, isInland);
        }catch (BizException e){
            log.error(DateFormatUtil.get(7,new Date())+"发送注册短信出现业务异常：{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"发送注册短信出现业务异常：{}",e.toString(),e);
            return new ResponseResult(new BizException("请稍后重试!"));
        }finally {
            if(lockFlag){
                lock.unlock();
            }
        }
    }

	@ApiOperation(value = "节点列表", httpMethod = "POST")
	@RequestMapping(value = "/node/list", method = RequestMethod.POST)
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	public ResponseResult<List<NodeDicRes>> findNodeDic(@RequestBody ServerNodeReq serverNodeReq) {
		return registerLoginService.findNodeDic(serverNodeReq);
	}

	@ApiOperation(value = "国家列表", httpMethod = "GET")
	@GetMapping(value = "/country/list")
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
    @ResponseBody
	public ResponseResult<List<CountryDicRes>> findCountryDic() {
		return registerLoginService.findCountryDic();
	}

	@ApiOperation(value = "注册", httpMethod = "POST")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	public ResponseResult register(@RequestBody Map<String, String> paramMap) {
		try {
			RegisterReq registerReq = registerHandler.decodeDataToObject(paramMap);
			registerReq.checkParam();
			registerHandler.checkAdPercent(registerReq.getAdPercent());
			// 检验这个手机号码是否具有资格访问这个接口
			if (StringUtils.isNotBlank(registerReq.getPhone())) {
				checkSmsRuleHandler.doCheckSmsRuleHandler(
						SmsUtil.getCountryCodeAndPhone(registerReq.getPhone(), registerReq.getCountryCode()),
						SendRegisterTypeEnum.REGISTER.getType() + "");
			}
			// 验证节点是否在限制集合里面
			limitWithdrawHandler.doLimitServerNodeId(registerReq);
			boolean isInland = true;
			if (nodeVoyageUtil.belongVoyageNode(registerReq.getNodeId()) && StringUtils.isNotBlank(registerReq.getPhone())) { //手机号注册
				// 航海计划节点
				registerHandler.judgeAbroadNodeIsRegister(registerReq);
				isInland = false;
			}else if(nodeVoyageUtil.belongVoyageNode(registerReq.getNodeId()) && StringUtils.isNotBlank(registerReq.getEmail())) { //邮箱注册
				// 航海计划节点
				registerHandler.judgeAbroadNodeIsEmailRegister(registerReq.getNodeId(),registerReq.getEmail());
				isInland = false;
			}
			return registerLoginService.register(registerReq, isInland);
		} catch (BizException e) {
			log.error(DateFormatUtil.get(7, new Date()) + "注册出现业务异常:{}", e.getMsg(), e);
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error(DateFormatUtil.get(7, new Date()) + "注册出现异常:{}", e.toString(), e);
			return new ResponseResult(new BizException("注册出现异常,请稍后重试!"));
		}
	}

	@ApiOperation(value = "登录", httpMethod = "POST")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	@EncryptionAnnotation
	public ResponseResult<UserInfoRes> login(
			@ApiParam(name = "req", value = "在原有基础上增加字段：asSubPre，并且把所有字段放在data里面，{\"data\":\"已加密字符串\"}") @RequestBody Map<String, String> paramMap) {
		long startTime = System.currentTimeMillis();
		try {
			LoginReq loginReq = JSONObject.parseObject(paramMap.get("data"), LoginReq.class);
			String requestHeader = request.getHeader("DeviceNumber");

			if (null == loginReq || StringUtils.isBlank(requestHeader)) {
				throw new BizException("参数检验错误,请再尝试下!");
			}

			loginReq.setDeviceNumber(requestHeader);
			loginReq.checkParam4MemberLogin();

			String redisValue = redisClient.get(requestHeader);
			if (1 == loginReq.getType().intValue()
					&& (StringUtils.isBlank(redisValue) || !redisValue.equals(loginReq.getAdPercent()))) {
				throw new BizException("人机参数检验异常!");
			}

			loginReq.setLoginIp(SystemUtils.getIpAddress(request));
			ResponseResult responseResult = registerLoginService.login(loginReq);
			long endTime = System.currentTimeMillis();
			log.info("登录接口总耗时:{}", endTime - startTime);
			return responseResult;
		} catch (BizException e) {
			return new ResponseResult(e);
		} catch (Exception e) {
			log.error("异常:{}", e.toString(), e);
			return new ResponseResult(new BizException("登录异常"));
		}
	}

	@ApiOperation(value = "人机广告验证", httpMethod = "GET")
	@GetMapping(value = "/manMachine/adVerify")
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	public ResponseResult manMachineAdVerify() {
		try {
			// 生成随机数
			String randomCode = EncryptionUtil.getRandomCode(6);
			String requestHeader = request.getHeader("DeviceNumber");
			redisClient.set(requestHeader, randomCode);
			// 获取对象-转换vo,及加密
			AdProofDiagramVo adProofDiagramVo = adProofDiagramService.getRandomOneAd();
			adProofDiagramVo.setAdPercent(randomCode);
			String result = JSONObject.toJSONString(adProofDiagramVo);
			return new ResponseResult(adProofDiagramService.doRsaEncrypt(result));
		} catch (Exception e) {
			log.error("人机广告验证获取异常:{}", e.toString(), e);
			return new ResponseResult(new BizException("人机广告验证获取异常"));
		}
	}

	@ApiOperation(value = "忘记密码", httpMethod = "POST")
	@RequestMapping(value = "/forget/pwd", method = RequestMethod.POST)
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	@EncryptionAnnotation
	public ResponseResult forgetPwd(@RequestBody Map<String, String> paramMap) {
		RegisterReq req = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
		req.checkForgetPwdParam();
		return registerLoginService.forgetPwd(req);
	}

	@ApiOperation(value = "修改登录密码", httpMethod = "POST")
	@RequestMapping(value = "/update/pwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult updatePwd(@RequestBody UpdatePwdReq req) {
		req.checkParam();
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.updatePwd(req);
	}

	@ApiOperation(value = "修改支付密码", httpMethod = "POST")
	@RequestMapping(value = "/update/payPwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult updatePayPwd(@RequestBody UpdatePwdReq req) {
		req.checkParam();
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.updatePayPwd(req);
	}

	@ApiOperation(value = "忘记支付密码", httpMethod = "POST")
	@RequestMapping(value = "/forget/payPwd", method = RequestMethod.POST)
	@ResponseBody
	@EncryptionAnnotation
	public ResponseResult forgetPayPwd(@RequestBody Map<String, String> paramMap) {
		UpdatePwdReq req = JSONObject.parseObject(paramMap.get("data"), UpdatePwdReq.class);
		req.checkForgetPayPwdParam();
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.forgetPayPwd(req);
	}

	@ApiOperation(value = "修改昵称和头像", httpMethod = "POST")
	@RequestMapping(value = "/update/user", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult updateUser(@RequestBody UpdateUserReq req) {
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.updateUser(req);
	}

	@ApiOperation(value = "邀请码", httpMethod = "POST")
	@RequestMapping(value = "/invitation/code", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult<UserCodeRes> findUserCode() {
		return registerLoginService.findUserCode(jwtUtil.getUserId(request));
	}

	@ApiOperation(value = "我的邀请", httpMethod = "POST")
	@RequestMapping(value = "/invitation/list", method = RequestMethod.POST)
	@ResponseBody
	public PageResult<List<UserInfoRes>> findUserShare(@RequestBody IdReq req) {
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.findUserShare(req);
	}

	@ApiOperation(value = "用户认证信息", httpMethod = "POST")
	@RequestMapping(value = "/authentication/info", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult<AuthenticationRes> userAuthentication(@RequestBody UserAuthReq req) {
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.userAuthentication(req);
	}

	@ApiOperation(value = "添加极光账号", httpMethod = "POST")
	@RequestMapping(value = "/update/jid", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult updateJiGuangId(@RequestBody UpdateUserReq req) {
		req.setUid(jwtUtil.getUserId(request));
		req.setAvatar(null);
		req.setName(null);
		return registerLoginService.updateUser(req);
	}

	@ApiOperation(value = "用户是否设置了 支付密码", httpMethod = "POST")
	@RequestMapping(value = "/has/paypwd", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult hasPayPwd() {
		return registerLoginService.hasPayPwd(jwtUtil.getUserId(request));
	}

	@ApiOperation(value = "修改手机号(验证码 修改)", httpMethod = "POST")
	@RequestMapping(value = "/change/phone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult changePhone(@RequestBody ChangePhoneReq req) {
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.changePhone(req);
	}

	@ApiOperation(value = "修改手机号(上传身份 信息)", httpMethod = "POST")
	@RequestMapping(value = "/change/idphone", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult changePhone1(@RequestBody ChangePhoneIdcardReq req) {
		req.setUid(jwtUtil.getUserId(request));
		return registerLoginService.changePhone(req);
	}

	@ApiOperation(value = "更换手机号审核状态", httpMethod = "POST")
	@RequestMapping(value = "/change/status", method = RequestMethod.POST)
	@ResponseBody
	public ResponseResult changePhoneStatus() {
		return registerLoginService.changePhoneStatus(jwtUtil.getUserId(request));
	}

	@ApiOperation(value = "发送邮箱验证码", httpMethod = "POST")
	@RequestMapping(value = "/mail/send", method = RequestMethod.POST)
	@ResponseBody
	@PassToken
	@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
	public ResponseResult sendRegisterMail(@RequestBody MailSendReq req) {
		if (!req.getEmail()
				.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")) {
			throw new BizException("邮箱格式有误!");
		}
		//当已登录拿登录相关信息
		if (-1 != jwtUtil.getNodeIdCompatException(request,-1L).intValue()) {
			req.setNodeId(jwtUtil.getNodeId(request));
		}
		try {
			String sendEmailTrue = adminOptionsUtil
					.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SEND_EMAIL_TRUE.getBelongsSystemCode());
			registerHandler.checkWhetherNeedSendEmail(request, sendEmailTrue, req);
			if (null != req.getUseType()) {
				if (MailTemplateEnum.BIND_TYPE.getCode().equals(req.getUseType())) {
					int result = mailBindService.findEmailExists(req.getEmail(), req.getNodeId());
					// 0：符合绑定要求，1：国内用户绑定不合规时的情况，2：大航海用户绑定不合规时的情况
					if (1 == result) {
						return ResponseResult
								.builder().code(GlobalConstant.ResponseCode.FAIL).msg("该邮箱当前节点绑定数量超过"
										+ serverNodeService.getById(req.getNodeId()).getEmailBindAmount() + "个")
								.build();
					} else if (2 == result) {
						return ResponseResult.builder().code(GlobalConstant.ResponseCode.FAIL)
								.msg("当前邮箱绑定次数已达上限，请更换邮箱绑定").build();
					}
				}
				aliyunEmailService.checkOftenFlag(req.getEmail(), req.getUseType());
				String emailCode = EncryptionUtil.getRandomCode(6);
				boolean flag = aliyunEmailService.sendEmailCode(req.getEmail(), emailCode, req.getUseType());
				// 邮箱验证码-KEY
				String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + req.getEmail() + "_" + req.getUseType();
				redisClient.set(emailKey, emailCode, 8 * 60);
				if (!flag) {
					throw new BizException("发送邮箱验证码出现异常，请稍候重试!");
				}
			}
			return new ResponseResult(new BizException(ErrorInfoEnum.SUCCESS));
		} catch (BizException e) {
			log.error(DateFormatUtil.getByNowTime(7)+"发送邮箱验证码出现业务异常：{}",
					JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.getMsg())));
			return new ResponseResult(ErrorInfoEnum.FAILED.getCode(), e.getMsg());
		} catch (Exception e) {
			log.error(DateFormatUtil.getByNowTime(7)+"发送邮箱验证码出现系统异常：{}",
					JSONObject.toJSONString(new ResponseResult(ErrorInfoEnum.PARAM_ERR.getCode(), e.toString())));
			return new ResponseResult(new BizException("发送邮箱验证码出现异常，请稍后重试!"));
		}
		// 其他的是注册类型 -- 兼容其他接口
		// return registerLoginService.sendMailCode(req);
	}
}
