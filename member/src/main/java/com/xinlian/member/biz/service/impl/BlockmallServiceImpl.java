package com.xinlian.member.biz.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TBlockmallPayCallbackErrorLog;
import com.xinlian.biz.model.TUserInfo;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CallbackTimeIntervalEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.SendRegisterTypeEnum;
import com.xinlian.common.enums.UserLevelStatusEnum;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.request.CatWalletPayReq;
import com.xinlian.common.request.CatWalletPayStatusReq;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.response.CatWalletPayCallbackErrorRes;
import com.xinlian.common.response.CatWalletPayCallbackRes;
import com.xinlian.common.response.CatWalletPayRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.jwt.util.EncryptionUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.BlockmallService;
import com.xinlian.member.biz.service.TBlockmallPayCallbackErrorLogService;
import com.xinlian.member.server.controller.handler.CheckSmsRuleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class BlockmallServiceImpl implements BlockmallService {

    @Autowired
    private TUserInfoMapper userInfoMapper;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SendSmsLogService sendSmsLogService;

    @Autowired
    private CheckSmsRuleHandler checkSmsRuleHandler;

    @Autowired
    private TWalletInfoMapper walletInfoMapper;

    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;

    @Value("${swaggerAuth}")
    private String swaggerAuth;

    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Autowired
    private TBlockmallPayCallbackErrorLogService tBlockmallPayCallbackErrorLogService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseResult<TUserInfo> cert(RegisterReq registerReq) {
        ResponseResult<TUserInfo> result = new ResponseResult<TUserInfo>();
        result.setCode(GlobalConstant.ResponseCode.FAIL);
        result.setResult(new TUserInfo());

        TUserInfo userInfoParam = new TUserInfo();
        userInfoParam.setUserName(registerReq.getUsername());
        userInfoParam.setServerNodeId(registerReq.getNodeId());
        TUserInfo userInfoRes = userInfoMapper.getOneModel(userInfoParam);
        if (null == userInfoRes) {
            log.info("::::用户不存在");
            result.setMsg("用户不存在");
            return result;
        }

        if (!registerReq.getPhone().equals(userInfoRes.getMobile())) {
            log.info("::::用户手机号不正确， phone = " + registerReq.getPhone() + ", mobile = " + userInfoRes.getMobile());
            result.setMsg("用户手机号不正确");
            return result;
        }
        String pwd = EncryptionUtil.md5Two(registerReq.getPassword(), userInfoRes.getSalt());
        if (!userInfoRes.getLoginPassWord().equals(pwd)) {
            log.info("::::登录密码或用户名有误");
            result.setMsg("登录密码或用户名有误");
            return result;
        }

        // 验证 验证码
        String phone = registerReq.getCountryCode() == 86 ? registerReq.getPhone()
                : registerReq.getCountryCode() + registerReq.getPhone();
        String redisPhoneKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(registerReq.getType(), phone));
        String redisCode = redisClient.get(redisPhoneKey);

        log.info("::::redisPhoneKey = " + redisPhoneKey + ", redisCode = " + redisCode + ", code = "
                + registerReq.getCode());

        // 所有注册验证码请求都记录下来
        sendSmsLogService.saveCheckErrorSmsCode(redisPhoneKey, redisCode, phone, registerReq);
        if (redisCode == null || !redisCode.equals(registerReq.getCode())) {
            result.setMsg("验证码有误");
            checkSmsRuleHandler.doSaveSmsRuleHandler(phone, SendRegisterTypeEnum.BLOCKMALL_CERT.getType() + "");
            return result;
        }
        checkSmsRuleHandler.doDeleteSmsRuleHandler(phone, SendRegisterTypeEnum.BLOCKMALL_CERT.getType() + "");

        // new 新对象，简单赋值
        TUserInfo newTUserInfo = new TUserInfo();
        newTUserInfo.setUid(userInfoRes.getUid());
        newTUserInfo.setUserName(userInfoRes.getUserName());
        newTUserInfo.setMobile(userInfoRes.getMobile());
        if (StringUtils.isBlank(userInfoRes.getRegtime())) {
            newTUserInfo.setRegtime(DateUtil.now());
        } else {
            try {
                newTUserInfo.setRegtime(DateUtil.format(
                        new Date(Long.valueOf(userInfoRes.getRegtime()) * 1000L), DatePattern.NORM_DATETIME_PATTERN));
            } catch (Exception e) {
                newTUserInfo.setRegtime(DateUtil.now());
            }
        }

        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(newTUserInfo);
        log.info("::::" + result.toString());
        return result;
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void pay(CatWalletPayReq catWalletPayReq) {
        CatWalletPayRes catWalletPayRes = payAsync(catWalletPayReq);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        CatWalletPayCallbackRes catWalletPayCallbackRes = null;
        try {
            String data = JSONObject.toJSONString(catWalletPayRes);
            String sign = RSAEncrypt.sign(data.getBytes(), getPrivateKey());
            catWalletPayCallbackRes = new CatWalletPayCallbackRes(data, sign);
        } catch (Exception e) {
        }

        HttpEntity<CatWalletPayCallbackRes> entity = new HttpEntity<CatWalletPayCallbackRes>(catWalletPayCallbackRes, headers);

        Boolean flag = false;//true 时主动回滚
        try {
            log.info("::::支付结果回调，回调地址：" + catWalletPayReq.getCallback() + ", orderNo：" + catWalletPayReq.getOrderNo());
            String str = restTemplate.postForObject(catWalletPayReq.getCallback(), entity, String.class);
            asyncSaveLog(catWalletPayReq, catWalletPayCallbackRes, str);
            if (StringUtils.isNotBlank(str)) {
                Integer code = JSONObject.parseObject(str).getInteger("code");
                if (code.equals(200)) {//成功
                    // 删除重试redis
                    redisClient.deleteByKey(RedisKeys.repeatPayCallbackErrorKey(catWalletPayRes.getOrderNo()));
                    return;
                } else if (code.equals(501)) {//业务异常 时主动回滚
                    flag = true;
                    log.info("::::支付失败, orderNo：" + catWalletPayReq.getOrderNo() + ", " + str);
                }
            }
        } catch (Exception e) {
            asyncSaveLog(catWalletPayReq, catWalletPayCallbackRes, e.getMessage());
            log.info("::::支付结果回调异常，回调地址：" + catWalletPayReq.getCallback() + ", orderNo：" + catWalletPayReq.getOrderNo()
                    + ", 异常信息： " + e.getMessage());
        }

        if (flag) {
            redisClient.deleteByKey(RedisKeys.repeatPayCallbackErrorKey(catWalletPayRes.getOrderNo()));
            redisClient.deleteByKey(RedisKeys.blockmallOrderKey(catWalletPayRes.getOrderNo()));
            redisClient.deleteByKey(RedisKeys.blockmallOrderDetailKey(catWalletPayRes.getOrderNo()));
            redisClient.set(RedisKeys.blockmallUnPayOrderKey(catWalletPayReq.getOrderNo()), catWalletPayReq.getOrderNo());
            throw new RuntimeException("支付失败");
        }

        // 重试
        CatWalletPayCallbackErrorRes catWalletPayCallbackErrorRes = new CatWalletPayCallbackErrorRes(CallbackTimeIntervalEnum.ONE.getCount(),
                catWalletPayReq.getCallback(), System.currentTimeMillis() + CallbackTimeIntervalEnum.ONE.getTime(),
                catWalletPayReq.getOrderNo(), catWalletPayCallbackRes);
        redisClient.set(RedisKeys.repeatPayCallbackErrorKey(catWalletPayRes.getOrderNo()), JSONObject.toJSONString(catWalletPayCallbackErrorRes));
    }

    private void asyncSaveLog(CatWalletPayReq catWalletPayReq, CatWalletPayCallbackRes catWalletPayCallbackRes, String errMsg) {
        TBlockmallPayCallbackErrorLog tblog = new TBlockmallPayCallbackErrorLog();
        tblog.setRepeateNum(0);
        tblog.setCallback(catWalletPayReq.getCallback());
        tblog.setCallbackTime(new Date());
        tblog.setOrderNo(catWalletPayReq.getOrderNo());
        tblog.setErrMsg(errMsg);
        tblog.setData(catWalletPayCallbackRes.getData());
        tblog.setSign(catWalletPayCallbackRes.getSign());
        try {
            tBlockmallPayCallbackErrorLogService.insert(tblog);
        } catch (Exception e) {
        }
    }

    private String getPrivateKey() {
        String privateKey = "";
        if (null != swaggerAuth && "gray".equals(swaggerAuth)) {
            privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_GRAY_PRIVATE_KEY.getBelongsSystemCode());
        } else {
            privateKey = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.APP_RSA_PRIVATE_KEY.getBelongsSystemCode());
        }
        return privateKey;
    }

    private CatWalletPayRes payAsync(CatWalletPayReq catWalletPayReq) {
        // 订单号10秒内防重复校验
        checkTempOrderNo(catWalletPayReq);

        // 订单号是否已支付校验
        checkOrderNo(catWalletPayReq);

        // 用户信息校验（手机号，手机号验证码，支付密码，激活状态）
        TUserInfo userInfoRes = checkUserInfo(catWalletPayReq);

        // 账户余额校验
        checkBalanceNum(catWalletPayReq, userInfoRes);

        // 扣减账户余额
        reduceBalanceNum(catWalletPayReq, userInfoRes);

        // 账户余额扣减后再校验
        checkBalanceNumAfterReduce(userInfoRes);

        // 保存订单流水信息
        Long tradeOrderId = saveTradeOrder(catWalletPayReq, userInfoRes);

        // 已支付订单号放缓存,并将其从未支付缓存中删除
        redisClient.set(RedisKeys.blockmallOrderKey(catWalletPayReq.getOrderNo()), catWalletPayReq.getOrderNo());
        redisClient.deleteByKey(RedisKeys.blockmallUnPayOrderKey(catWalletPayReq.getOrderNo()));

        CatWalletPayRes catWalletPayRes = generateCatWalletPayRes(catWalletPayReq, userInfoRes, tradeOrderId);
        redisClient.set(RedisKeys.blockmallOrderDetailKey(catWalletPayReq.getOrderNo()), JSONObject.toJSONString(catWalletPayRes), 1800);

        log.info("::::" + catWalletPayRes.toString());
        return catWalletPayRes;
    }

    private Long saveTradeOrder(CatWalletPayReq catWalletPayReq, TUserInfo userInfoRes) {
        TWalletTradeOrder walletTradeOrder = generateTWalletTradeOrder(catWalletPayReq, userInfoRes);
        Integer saveI = walletTradeOrderMapper.saveModel(walletTradeOrder);
        if (null == saveI || saveI < 1) {
            log.info("::::抱歉，该账户支付失败，交易流水入库失败");
            throw new BizException("抱歉，该账户支付失败");
        }
        return walletTradeOrder.getId();
    }

    private void checkBalanceNumAfterReduce(TUserInfo userInfoRes) {
        TWalletInfo twalletInfoAfterPay = walletInfoMapper.queryWalletByUid(userInfoRes.getUid(),
                CurrencyEnum.CAT.getCurrencyCode());
        if (null == twalletInfoAfterPay || null == twalletInfoAfterPay.getBalanceNum()
                || twalletInfoAfterPay.getBalanceNum().compareTo(BigDecimal.ZERO) < 0) {
            log.info("::::抱歉，该账户支付失败，数据库账户余额为负");
            throw new BizException("抱歉，该账户支付失败");
        }
    }

    private void reduceBalanceNum(CatWalletPayReq catWalletPayReq, TUserInfo userInfoRes) {
        int updateI = walletInfoMapper.updateReduceBalanceNum(new BigDecimal(catWalletPayReq.getPayAmount()), userInfoRes.getUid(),
                Long.valueOf(Integer.toString(CurrencyEnum.CAT.getCurrencyId())));
        if (updateI < 1) {
            log.info("::::抱歉，该账户支付失败，更新数据库失败");
            throw new BizException("抱歉，该账户支付失败");
        }
    }

    private void checkBalanceNum(CatWalletPayReq catWalletPayReq, TUserInfo userInfoRes) {
        TWalletInfo twalletInfo = walletInfoMapper.queryWalletByUid(userInfoRes.getUid(),
                CurrencyEnum.CAT.getCurrencyCode());
        if (null == twalletInfo || StringUtils.isBlank(catWalletPayReq.getPayAmount()) || null == twalletInfo.getBalanceNum()
                || new BigDecimal(catWalletPayReq.getPayAmount()).compareTo(BigDecimal.ZERO) < 1
                || twalletInfo.getBalanceNum().compareTo(BigDecimal.ZERO) < 1
                || twalletInfo.getBalanceNum().compareTo(new BigDecimal(catWalletPayReq.getPayAmount())) < 0) {
            log.info("::::抱歉，该账户余额不足");
            throw new BizException("重复订单号，终止支付");
        }
    }

    private TUserInfo checkUserInfo(CatWalletPayReq catWalletPayReq) {
        TUserInfo userInfoParam = new TUserInfo();
        userInfoParam.setUid(catWalletPayReq.getUid());
        TUserInfo userInfoRes = userInfoMapper.getOneModel(userInfoParam);

        // 1、验证账号是否存在
        if (null == userInfoRes) {
            log.info("::::用户不存在");
            throw new BizException("用户不存在");
        }

        // 2、验证手机号是否正确
        if (!catWalletPayReq.getPhone().equals(userInfoRes.getMobile())) {
            log.info("::::用户手机号不正确， phone = " + catWalletPayReq.getPhone() + ", mobile = " + userInfoRes.getMobile());
            throw new BizException("用户手机号不正确");
        }

        // 3、验证支付密码是否正确
        String pwd = EncryptionUtil.md5Two(catWalletPayReq.getDealPsw(), userInfoRes.getSalt());
        if (!userInfoRes.getPayPassWord().equals(pwd)) {
            log.info("::::支付密码或用户名有误");
            throw new BizException("支付密码或用户名有误");
        }

        // 4、验证手机验证码是否正确
        String phone = catWalletPayReq.getCountryCode() == 86 ? catWalletPayReq.getPhone()
                : catWalletPayReq.getCountryCode() + catWalletPayReq.getPhone();
        String redisPhoneKey = SmsUtil.createPhoneKey(RedisKeys.createSmsPhoneKey(catWalletPayReq.getType(), phone));
        String redisCode = redisClient.get(redisPhoneKey);
        log.info("::::redisPhoneKey = " + redisPhoneKey + ", redisCode = " + redisCode + ", code = "
                + catWalletPayReq.getCode());

        // 所有注册验证码请求都记录下来
        RegisterReq registerReq = new RegisterReq();
        registerReq.setCode(catWalletPayReq.getCode());
        registerReq.setCountryCode(catWalletPayReq.getCountryCode());
        sendSmsLogService.saveCheckErrorSmsCode(redisPhoneKey, redisCode, phone, registerReq);
        if (redisCode == null || !redisCode.equals(registerReq.getCode())) {
            checkSmsRuleHandler.doSaveSmsRuleHandler(phone, SendRegisterTypeEnum.BLOCKMALL_PAY.getType() + "");
            throw new BizException("验证码有误");
        }
        checkSmsRuleHandler.doDeleteSmsRuleHandler(phone, SendRegisterTypeEnum.BLOCKMALL_PAY.getType() + "");

        // 5、验证客户激活状态
        if (UserLevelStatusEnum.FREEZE.getCode() == userInfoRes.getLevelStatus()) {
            log.info("::::抱歉，该账户已被冻结!");
            throw new BizException("抱歉，该账户已被冻结!");
        }

        return userInfoRes;
    }

    private void checkOrderNo(CatWalletPayReq catWalletPayReq) {
        String blockmallOrderKey = RedisKeys.blockmallOrderKey(catWalletPayReq.getOrderNo());
        String blockmallOrderValue = redisClient.get(blockmallOrderKey);
        if (StringUtils.isNotBlank(blockmallOrderValue)) {
            log.info("::::重复订单号，终止支付");
            throw new BizException("重复订单号，终止支付");
        } else {
            TWalletTradeOrder whereModel = new TWalletTradeOrder();
            whereModel.setTradeAddress(catWalletPayReq.getOrderNo());
            TWalletTradeOrder twalletTradeOrderInDB = walletTradeOrderMapper.getByCriteria(whereModel);
            if (null != twalletTradeOrderInDB) {//已支付订单
                redisClient.set(blockmallOrderKey, catWalletPayReq.getOrderNo());
                redisClient.deleteByKey(RedisKeys.blockmallUnPayOrderKey(catWalletPayReq.getOrderNo()));
                log.info("::::重复订单号，终止支付");
                throw new BizException("重复订单号，终止支付");
            } else {//未支付订单
                redisClient.set(RedisKeys.blockmallUnPayOrderKey(catWalletPayReq.getOrderNo()), catWalletPayReq.getOrderNo());
            }
        }
    }

    private void checkTempOrderNo(CatWalletPayReq catWalletPayReq) {
        String blockmallTempOrderKey = RedisKeys.blockmallTempOrderKey(catWalletPayReq.getOrderNo());
        String blockmallTempOrderValue = redisClient.get(blockmallTempOrderKey);
        if (StringUtils.isNotBlank(blockmallTempOrderValue)) {
            log.info("::::重复订单号，终止支付 " + catWalletPayReq.getOrderNo());
            throw new BizException("重复订单号，终止支付");
        } else {
            redisClient.set(blockmallTempOrderKey, catWalletPayReq.getOrderNo(), 10L);
        }

        Long timeOut = redisClient.get(RedisKeys.blockmallUnTimeoutOrderKey(catWalletPayReq.getOrderNo()));
        if (null == timeOut || timeOut.compareTo(System.currentTimeMillis() / 1000) < 0) {
            log.info("::::订单已过期，终止支付 " + catWalletPayReq.getOrderNo());
            throw new BizException("订单已过期，终止支付");
        }
    }

    private CatWalletPayRes generateCatWalletPayRes(CatWalletPayReq catWalletPayReq, TUserInfo userInfoRes, Long tradeOrderId) {
        CatWalletPayRes catWalletPayRes = new CatWalletPayRes();
        catWalletPayRes.setUid(userInfoRes.getUid());
        catWalletPayRes.setNodeId(userInfoRes.getServerNodeId());
        catWalletPayRes.setOrderNo(catWalletPayReq.getOrderNo());
        catWalletPayRes.setPayAmount(catWalletPayReq.getPayAmount());
        catWalletPayRes.setCatTradeOrderId(tradeOrderId);
        catWalletPayRes.setPayStatus(1);
        return catWalletPayRes;
    }

    private TWalletTradeOrder generateTWalletTradeOrder(CatWalletPayReq catWalletPayReq, TUserInfo userInfoRes) {
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setUid(userInfoRes.getUid());
        walletTradeOrder.setCurrencyId(Long.valueOf(Integer.toString(CurrencyEnum.CAT.getCurrencyId())));
        walletTradeOrder.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
        walletTradeOrder.setTradeAddress(catWalletPayReq.getOrderNo());
        walletTradeOrder.setTradeCurrencyNum(new BigDecimal(catWalletPayReq.getPayAmount()));
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.BLOCKMALL_PAY.getTradeType());
        walletTradeOrder.setDes(WalletTradeTypeEnum.BLOCKMALL_PAY.getTradeDesc());
        walletTradeOrder.setCreateTime(new Date());
        return walletTradeOrder;
    }

    @Override
    public ResponseResult<CatWalletPayCallbackRes> payStatus(CatWalletPayStatusReq catWalletPayStatusReq) {
        try {
            CatWalletPayRes catWalletPayRes = queryPayStatus(catWalletPayStatusReq);
            String data = JSONObject.toJSONString(catWalletPayRes);
            String sign = RSAEncrypt.sign(data.getBytes(), getPrivateKey());

            return ResponseResult.ok(new CatWalletPayCallbackRes(data, sign));
        } catch (Exception e) {
            throw new BizException("查询失败");
        }
    }


    private CatWalletPayRes queryPayStatus(CatWalletPayStatusReq catWalletPayStatusReq) {
        CatWalletPayRes catWalletPayRes = new CatWalletPayRes();
        catWalletPayRes.setPayStatus(0);
        if (null == catWalletPayStatusReq || StringUtils.isBlank(catWalletPayStatusReq.getOrderNo())) {
            return catWalletPayRes;
        }

        String blockmallOrderKey = RedisKeys.blockmallOrderKey(catWalletPayStatusReq.getOrderNo());
        String blockmallOrderValue = redisClient.get(blockmallOrderKey);
        String blockmallOrderDetailKey = RedisKeys.blockmallOrderDetailKey(catWalletPayStatusReq.getOrderNo());
        String blockmallOrderDetailValue = redisClient.get(blockmallOrderDetailKey);


        if (StringUtils.isNotBlank(blockmallOrderValue) && StringUtils.isNotBlank(blockmallOrderDetailValue)) {
            catWalletPayRes = JSONObject.parseObject(blockmallOrderDetailValue, CatWalletPayRes.class);
            if (null != catWalletPayRes && catWalletPayRes.getPayStatus() == 1) {
                return catWalletPayRes;
            }
        }

        String blockmallUnPayOrderKey = RedisKeys.blockmallUnPayOrderKey(catWalletPayStatusReq.getOrderNo());
        String blockmallUnPayOrderValue = redisClient.get(blockmallUnPayOrderKey);
        if (StringUtils.isNotBlank(blockmallUnPayOrderValue)) {
            return catWalletPayRes;
        }

        TWalletTradeOrder whereModel = new TWalletTradeOrder();
        whereModel.setTradeAddress(catWalletPayStatusReq.getOrderNo());
        TWalletTradeOrder twalletTradeOrderInDB = walletTradeOrderMapper.getByCriteria(whereModel);
        if (null != twalletTradeOrderInDB) {
            catWalletPayRes.setCatTradeOrderId(twalletTradeOrderInDB.getId());
            catWalletPayRes.setOrderNo(twalletTradeOrderInDB.getTradeAddress());
            catWalletPayRes.setPayAmount(twalletTradeOrderInDB.getTradeCurrencyNum().toString());
            catWalletPayRes.setUid(twalletTradeOrderInDB.getUid());
            catWalletPayRes.setNodeId(twalletTradeOrderInDB.getServerNodeId());
            catWalletPayRes.setPayStatus(1);

            redisClient.set(blockmallOrderKey, catWalletPayStatusReq.getOrderNo());
            redisClient.set(blockmallOrderDetailKey, JSONObject.toJSONString(catWalletPayRes), 1800);
            redisClient.deleteByKey(blockmallUnPayOrderKey);

            return catWalletPayRes;
        } else {
            redisClient.set(blockmallUnPayOrderKey, catWalletPayStatusReq.getOrderNo());
            return catWalletPayRes;
        }
    }


}
