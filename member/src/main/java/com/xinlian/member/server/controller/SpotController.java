package com.xinlian.member.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CheckSmsMethodEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.CheckPwdReq;
import com.xinlian.common.request.OrderStateReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.request.SellCatReq;
import com.xinlian.common.response.OrderOpenRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.jwt.annotate.EncryptionH5Annotation;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.SpotService;
import com.xinlian.member.biz.service.TOrderService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import com.xinlian.member.server.controller.handler.LimitWithdrawHandler;
import com.xinlian.member.server.controller.handler.SpotValidateHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * 现货交易
 */
@RestController
@RequestMapping("/spot")
@Api(value = "交易")
@Slf4j
public class SpotController {
	@Autowired
	private SpotService spotService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private IRegisterLoginService registerLoginService;
	@Autowired
	private TOrderService tOrderService;
	@Autowired
	private AdminOptionsUtil adminOptionsUtil;
	@Autowired
	private LimitWithdrawHandler limitWithdrawHandler;
	@Autowired
	private CheckSmsRuleHandler checkSmsRuleHandler;
	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	private SpotValidateHandler spotHandler;
	@Autowired
	private RedisLockRegistry redisLockRegistry;
	@Autowired
	private TUserInfoMapper tUserInfoMapper;

	@ApiOperation("出售Cat")
	@PostMapping("/sellCat")
	@EncryptionH5Annotation
	public ResponseResult sellCat(@RequestBody Map<String, String> paramMap) {
		//first. get decode rsa data
		SellCatReq sellCatReq = JSONObject.parseObject(paramMap.get("data"), SellCatReq.class);
		Long userId = jwtUtil.getUserId(httpServletRequest);
		//用户进来就记录日志
		log.info("用户{}发起挂单操作, 参数：{}", userId, JSON.toJSONString(sellCatReq));
		Lock lock = redisLockRegistry.obtain("SELL_CAT_".concat(userId.toString()));
		boolean redisLockFlag = true;
		try{
			//check repetition submit
			if(!lock.tryLock()){
				log.debug(Thread.currentThread().getName()+" : 请求出售CAT接口，获取分布式锁失败!");
				redisLockFlag = false;
				throw new BizException("重复提交请稍后再试!");
			}
			//check req param
			spotHandler.sellCatCheckRequestParam(sellCatReq);
			//second. check sms rule handler
			checkSmsRuleHandler.doCheckSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.SELL_CAT.getMethodCode());
			//three. black uid data
			limitWithdrawHandler.doLimitWithdraw(CurrencyEnum.CAT.getCurrencyId() + "", userId);
			//from token uid set
			sellCatReq.setUid(userId);
			return spotService.sellCat(sellCatReq);
		}catch (BizException e){
			log.error(DateFormatUtil.getByNowTime(7)+"-出售CAT出现业务异常:{}",e.getMsg(),e);
			return new ResponseResult(e);
		}catch (Exception e){
			log.error(DateFormatUtil.getByNowTime(7)+"-出售CAT出现系统异常:{}",e.toString(),e);
			return new ResponseResult(new BizException("请稍后重试!"));
		}finally {
			if(redisLockFlag) {
				lock.unlock();
				log.debug(Thread.currentThread().getName()+" : 出售CAT接口，释放分布式锁success");
			}
		}
	}

	@ApiOperation("校验节点是否可交易")
	@PostMapping("/checkCanTrade")
	public ResponseResult checkCanTrade(@RequestBody SellCatReq sellCatReq) {
		sellCatReq.setUid(jwtUtil.getUserId(httpServletRequest));
		return spotService.checkCanTrade(sellCatReq);
	}

	@ApiOperation("购买Cat")
	@PostMapping("/buyCat")
	@EncryptionH5Annotation
	public ResponseResult buyCat(@RequestBody Map<String, String> paramMap) {
		//first. get decode rsa data
		SellCatReq sellCatReq = JSONObject.parseObject(paramMap.get("data"), SellCatReq.class);
		Long userId = jwtUtil.getUserId(httpServletRequest);
		Lock lock = redisLockRegistry.obtain("BUY_CAT_".concat(userId.toString()));
		boolean redisLockFlag = true;
		try{
			//check repetition submit
			if(!lock.tryLock()){
				log.debug(Thread.currentThread().getName()+" : 请求购买CAT接口，获取分布式锁失败!");
				redisLockFlag = false;
				throw new BizException("重复提交请稍后再试!");
			}
			spotHandler.notInSpotTime();
			checkSmsRuleHandler.doCheckSmsRuleHandler(sellCatReq.getPhone(), CheckSmsMethodEnum.BUY_CAT.getMethodCode());
			limitWithdrawHandler.doLimitWithdraw(CurrencyEnum.CAT.getCurrencyId() + "", userId);
			sellCatReq.setUid(userId);
			return spotService.buyCat(sellCatReq);
		}catch (BizException e){
			log.error(DateFormatUtil.getByNowTime(7)+"-购买CAT出现业务异常:{}",e.getMsg(),e);
			return new ResponseResult(e);
		}catch (Exception e){
			log.error(DateFormatUtil.getByNowTime(7)+"-购买CAT出现系统异常:{}",e.toString(),e);
			return new ResponseResult(new BizException("请稍后重试!"));
		}finally {
			if(redisLockFlag) {
				lock.unlock();
				log.debug(Thread.currentThread().getName()+" : 购买CAT接口，释放分布式锁success");
			}
		}

	}

    @ApiOperation("查询订单状态")
    @PostMapping("/findOrderState")
    public ResponseResult findOrderState(@RequestBody OrderStateReq orderStateReq, HttpServletRequest httpServletRequest) {
        Long uid = jwtUtil.getUserId(httpServletRequest);
        orderStateReq.setUid(uid);
        return spotService.findOrderState(orderStateReq);
    }

	@ApiOperation("发送买入cat验证码")
	@PostMapping("/getDelCode")
	@EncryptionH5Annotation
	public ResponseResult getDelCode(@RequestBody Map<String, String> paramMap) {
		RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
		Long userId = jwtUtil.getUserId(httpServletRequest);
		TUserInfo userInfo = tUserInfoMapper.selectById(userId);
		//敏感信息从服务端查询
		registerReq.setPhone(userInfo.getMobile());
		registerReq.setCountryCode(userInfo.getCountryCode());
		spotHandler.notInSpotTime();
		//three. black uid data
		limitWithdrawHandler.doLimitWithdraw(CurrencyEnum.CAT.getCurrencyId() + "", userId);
		checkSmsRuleHandler.doCheckSmsRuleHandler(registerReq.getPhone(), CheckSmsMethodEnum.BUY_CAT.getMethodCode());
		registerReq.setType(14);// 设置为购买交易
		if (null == registerReq.getNodeId()) {
			registerReq.setNodeId(jwtUtil.getNodeId(httpServletRequest));
		}
		return registerLoginService.sendRegisterSms(registerReq, false);
	}

	@ApiOperation("验证卖出验证码")
	@PostMapping("checkSellCode")
	public ResponseResult checkSellCode(@RequestBody SellCatReq sellCatReq) {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		TUserInfo userInfo = tUserInfoMapper.selectById(userId);
		//敏感信息从服务端查询
		sellCatReq.setPhone(userInfo.getMobile());
		sellCatReq.setCountryCode(userInfo.getCountryCode());
		return spotService.checkSellCode(sellCatReq);
	}

	@ApiOperation("发送卖出cat验证码")
	@PostMapping("/getSellCode")
	@EncryptionH5Annotation
	public ResponseResult getSellCode(@RequestBody Map<String, String> paramMap) {
		RegisterReq registerReq = JSONObject.parseObject(paramMap.get("data"), RegisterReq.class);
		spotHandler.notInSpotTime();
		Long userId = jwtUtil.getUserId(httpServletRequest);
		TUserInfo userInfo = tUserInfoMapper.selectById(userId);
		//敏感信息从服务端查询
		registerReq.setPhone(userInfo.getMobile());
		registerReq.setCountryCode(userInfo.getCountryCode());
		checkSmsRuleHandler.doCheckSmsRuleHandler(registerReq.getPhone(), CheckSmsMethodEnum.SELL_CAT.getMethodCode());
		registerReq.setType(15);// 设置为sell交易
		if (null == registerReq.getNodeId()) {
			registerReq.setNodeId(jwtUtil.getNodeId(httpServletRequest));
		}
		return registerLoginService.sendRegisterSms(registerReq, false);
	}

	@ApiOperation("验证支付密码")
	@PostMapping("/checkPayPassword")
	public ResponseResult checkPayPassword(@RequestBody CheckPwdReq checkPwdReq) {
		Long userId = jwtUtil.getUserId(httpServletRequest);
		checkPwdReq.setUid(userId);
		return spotService.checkPayPassword(checkPwdReq);
	}

	@ApiOperation("根据订单号查询订单")
	@GetMapping("/queryOrderByOrderId")
	public ResponseResult queryOrderByOrderId(@RequestParam String orderId) {
		TOrder tOrder = tOrderService.selectOne(new EntityWrapper<TOrder>().eq("order_id", orderId));
		if (tOrder == null) {
			return ResponseResult.ok();
		}
		OrderOpenRes orderOpenRes = null;
		try {
			orderOpenRes = adminOptionsUtil.fieldEntityObject(
					AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("配置有误");
		}
		String orderTimeOut = orderOpenRes.getOrderTimeOut();
		long orderTimeOutL = Long.parseLong(orderTimeOut);
		tOrder.setTimeOutTime(tOrder.getCreateTime() + orderTimeOutL);
		return ResponseResult.ok(tOrder);
	}

	@ApiOperation("查询交易记录")
	@GetMapping("/findOrderRecord")
	public ResponseResult findOrderRecord() {
		Long uid = jwtUtil.getUserId(httpServletRequest);
		return spotService.findOrderRecord(uid);
	}

	/**
	 * @return
	 * @throws Exception
	 */
	@ApiOperation("验证当前时间是否在挂单时间段")
	@GetMapping("checkTime")
	@PassToken
	public ResponseResult checkTime() throws Exception {
		OrderOpenRes orderOpenRes = adminOptionsUtil.fieldEntityObject(
				AdminOptionsBelongsSystemCodeEnum.ORDER_TIME_OUT.getBelongsSystemCode(), OrderOpenRes.class);
		boolean timeRange = CommonUtil.isTimeRange(orderOpenRes);
		return ResponseResult.ok(timeRange);
	}



}
