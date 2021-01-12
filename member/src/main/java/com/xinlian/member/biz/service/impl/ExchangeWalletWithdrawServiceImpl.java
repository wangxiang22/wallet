package com.xinlian.member.biz.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TCurrencyManageMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.*;
import com.xinlian.common.contants.ExchangeConstant;
import com.xinlian.common.enums.CommonEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.enums.MailTemplateEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.request.ExchangeBalanceReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.HttpClientUtil;
import com.xinlian.member.biz.alisms.util.SmsUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.ExchangeWalletWithdrawService;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.TradeErrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExchangeWalletWithdrawServiceImpl implements ExchangeWalletWithdrawService {

    @Autowired
    private TCurrencyManageMapper tCurrencyManageMapper;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private IRegisterLoginService iRegisterLoginService;
    @Autowired
    private TWalletInfoMapper tWalletInfoMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TradeErrService tradeErrService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResponseResult withdraw(UserCurrencyStateReq userCurrencyStateReq) {
        ResponseResult userCurrencyState = getUserCurrencyState(userCurrencyStateReq);
        if (userCurrencyState.getCode()!=200){
            return userCurrencyState;
        }
        String smsCode = userCurrencyStateReq.getCode();
        String redisCode="";
        if (!StringUtils.isEmpty(userCurrencyStateReq.getEmail())){
            String emailKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + userCurrencyStateReq.getEmail() + "_" + MailTemplateEnum.EXCHANGE_WALLET_TRADE_TYPE.getCode();
            redisCode=redisClient.get(emailKey);
            redisClient.deleteByKey(RedisConstant.EMAIL_CODE_KEY_PREFIX + userCurrencyStateReq.getEmail() + "_" + MailTemplateEnum.EXCHANGE_WALLET_TRADE_TYPE.getCode());//删除键，防止重复同验证码请求
        }else {
            redisCode = redisClient.get(SmsUtil.createPhoneKey(userCurrencyStateReq.getPhone()) + "exWalletWithdraw");
            redisClient.deleteByKey(SmsUtil.createPhoneKey(userCurrencyStateReq.getPhone()) + "exWalletWithdraw");
        }
//        String smsCode = "123456";
//        String redisCode="123456";
        if (StringUtils.isEmpty(smsCode)){
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.CODE_NULL.getDes()).build();
        }
        if (StringUtils.isEmpty(redisCode)){
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.CODE_OUT_TIME.getDes()).build();
        }
        if (!smsCode.equals(redisCode)){
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.CODE_ERROR.getDes()).build();
        }

        Integer type = userCurrencyStateReq.getType();
        if (type==1) {//交易所转入钱包 钱包+钱 交易所-钱
            tWalletInfoMapper.despoit(userCurrencyStateReq);
            userCurrencyStateReq.setAmount(userCurrencyStateReq.getAmount().multiply(new BigDecimal(-1)));

        }else if (type==2) {
            //钱包转入交易所
            TWalletInfo tWalletInfo = new TWalletInfo();
            tWalletInfo.setUid(userCurrencyStateReq.getUid());
            tWalletInfo.setCurrencyId(userCurrencyStateReq.getCurrencyId());
            TWalletInfo walletResult = tWalletInfoMapper.selectOne(tWalletInfo);
            if (walletResult==null){
                throw new BizException("无该用户钱包信息");
            }
            BigDecimal balanceNum = walletResult.getBalanceNum()==null?BigDecimal.ZERO:walletResult.getBalanceNum();
            if (balanceNum.compareTo(userCurrencyStateReq.getAmount())==-1){
                //余额不足
                throw new BizException(CommonEnum.BALANCE_NOT_ENOUGH.getDes());
            }
            //钱包转入交易所 -钱
            tWalletInfoMapper.withdraw(userCurrencyStateReq);
        }

        Map map = new HashMap();
        map.put("uid",userCurrencyStateReq.getExchangeId());
        map.put("coinName",userCurrencyStateReq.getCoinName().toUpperCase());
        //传负数交易所扣钱，传正数交易所加钱
        map.put("amount",type==2?userCurrencyStateReq.getAmount().abs():userCurrencyStateReq.getAmount().abs().multiply(new BigDecimal(-1)));
        log.info("传入到slot："+map.toString());
        String post=HttpClientUtil.doJsonPost(ExchangeConstant.SOLT_URL, map,null);
        if (StringUtils.isEmpty(post)){
            tradeErrService.saveErrorLog(userCurrencyStateReq);
            throw new BizException(CommonEnum.FAILED_TO_TRADE.getDes());
        }
        log.info("slot返回："+post);
        JSONObject jsonObject = JSON.parseObject(post);
        Integer code = jsonObject.getInteger("code");
        if (code!=200){
            tradeErrService.saveErrorLog(userCurrencyStateReq);
            throw new BizException("交易失败");
        }
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(userCurrencyStateReq.getUid());
        tWalletTradeOrder.setTradeCurrencyNum(userCurrencyStateReq.getType()==1?userCurrencyStateReq.getAmount().abs():userCurrencyStateReq.getAmount().abs().multiply(new BigDecimal(-1)));
        tWalletTradeOrder.setCurrencyId(userCurrencyStateReq.getCurrencyId());
        tWalletTradeOrder.setCurrencyCode(userCurrencyStateReq.getCoinName().toUpperCase());
        tWalletTradeOrder.setCreateTime(new Date());
        tWalletTradeOrder.setTradeStatus(7);//交易成功
        tWalletTradeOrder.setTradeType(type);
        tWalletTradeOrder.setDes(type==1? WalletTradeTypeEnum.FROM_ROCKET.getTradeDesc():WalletTradeTypeEnum.TO_ROCKET.getTradeDesc());
        tWalletTradeOrder.setIsin(1);
        tWalletTradeOrderMapper.exchangeWalletTrade(tWalletTradeOrder);
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(new JSONObject()).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    @Override
    public ResponseResult getUserCurrencyState(UserCurrencyStateReq userCurrencyStateReq) {
         /*1.判断币种是否可以重提
         * 2.判断用户当日是否还有   冲提额度，冲提次数
           3.判断用户该笔冲提是否超过单笔限额*/
        TCurrencyManage tCurrencyManage = new TCurrencyManage();
        tCurrencyManage.setCurrencyId(userCurrencyStateReq.getCurrencyId());
        TCurrencyManage queryResult = tCurrencyManageMapper.selectOne(tCurrencyManage);
        if (queryResult == null) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.PARAM_NO.getDes()).build();
        }
        if (userCurrencyStateReq.getAmount().compareTo(queryResult.getWithdrawSingleAmount()) == 1) {
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.OUT_OF_SINGLE_TRADE.getDes()).build();
        }
        Integer type = userCurrencyStateReq.getType();
        switch (type) {
            //交易所转入钱包
            case 1:
                if (queryResult.getExToWallet() == 2) {
                    return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NO_PROMISION_EX_TO_WALLET.getDes()).build();
                }
                break;
            //钱包转入交易所
            case 2:
                if (queryResult.getWalletToEx() == 2) {
                    return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NO_PROMISION_WALLET_TO_EX.getDes()).build();
                }
                break;
        }
        //需要得到 交易所钱包当日交易次数，当日总金额
        WalletTradeCount walletTradeCount = tWalletTradeOrderMapper.queryToday(userCurrencyStateReq.getUid());
        BigDecimal allAmount = walletTradeCount.getAllAmount();
        Integer allCount = walletTradeCount.getAllCount();
        if (queryResult.getWithdrawTime() <= allCount) {
            //超过当日次数限制
            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NO_TIME_TRADE.getDes()).build();
        }
        if (type==2) {
            WalletTradeCount todayWithdrawAmount = tWalletTradeOrderMapper.queryTodayWithdrawAmount(userCurrencyStateReq.getUid());
            if (null==todayWithdrawAmount.getAllAmount()){
                todayWithdrawAmount.setAllAmount(BigDecimal.ZERO);
            }
            BigDecimal dbUserTodayAmount = todayWithdrawAmount.getAllAmount();//用户当日历史提现总额
            BigDecimal add = dbUserTodayAmount.add(userCurrencyStateReq.getAmount());//本次提现金额
            //如果用户今日提现历史记录加上该笔提现超出当日限制
            if (queryResult.getWithdrawAmount().compareTo(add)==-1){
                return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NO_AMOUNT_OF_MONEY.getDes()).build();
            }
        }else {
            WalletTradeCount todayDespositAmount = tWalletTradeOrderMapper.queryTodayDespositAmount(userCurrencyStateReq.getUid());
            if (null==todayDespositAmount.getAllAmount()){
                todayDespositAmount.setAllAmount(BigDecimal.ZERO);
            }
            BigDecimal dbUserTodayAmount = todayDespositAmount.getAllAmount();//用户当日历史充值总额
            BigDecimal add = dbUserTodayAmount.add(userCurrencyStateReq.getAmount());//本次充值金额
            if (queryResult.getDespositAmount().compareTo(add)==-1){
                return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.OUT_OF_TODAY_DESPOIT.getDes()).build();
            }
        }
//        if (queryResult.getWithdrawAmount().compareTo(allAmount) < 1) {
//            return ResponseResult.builder().code(ErrorCode.REQ_ERROR.getCode()).result(new JSONObject()).msg(CommonEnum.NO_AMOUNT_OF_MONEY.getDes()).build();
//        }
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(null).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }



    @Override
    public ResponseResult getCurrencyBalance(UserCurrencyStateReq userCurrencyStateReq) {
        CurrencyStateBalance currencyStateBalance=tCurrencyManageMapper.queryBalance(userCurrencyStateReq);
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(currencyStateBalance).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    @Override
    public ResponseResult withdrawHistory(Long userId) {
        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setIsin(1);
        tWalletTradeOrder.setUid(userId);
        List<TWalletTradeOrder> tWalletTradeOrders = tWalletTradeOrderMapper.selectList(new EntityWrapper<TWalletTradeOrder>().eq("isin", 1).eq("uid", userId));
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(tWalletTradeOrders).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    @Override
    public ResponseResult getExchangeBalance(ExchangeBalanceReq exchangeBalanceReq) {
        Map map = new HashMap();
        map.put("uid",exchangeBalanceReq.getUid());
        map.put("coinName",exchangeBalanceReq.getCoinName());
        log.info("发起查询交易所用户信息");
        String post=HttpClientUtil.doJsonPost(ExchangeConstant.EXCHANGE_BALANCE, map,null);
        log.info("查询交易所用户信息："+post);
        JSONObject jsonObject = JSON.parseObject(post);
        if (jsonObject.getInteger("code")!=200){
            throw new BizException("查询交易所余额失败，请稍后重试");
        }
        return ResponseResult.builder().code(ErrorCode.REQ_SUCCESS.getCode()).result(jsonObject).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }
}
