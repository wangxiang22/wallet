package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.biz.dao.*;
import com.xinlian.biz.model.*;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.CurrencyManagerCashEnum;
import com.xinlian.common.enums.ErrorCode;
import com.xinlian.common.request.CurrencyInfoRes;
import com.xinlian.common.request.GetOneTradeInfoReq;
import com.xinlian.common.request.TradeInfoReq;
import com.xinlian.common.response.ChainOwnerAssetRes;
import com.xinlian.common.response.CurrencyBalanceRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.UserBalanceService;
import com.xinlian.member.biz.service.WithdrawCustomerService;
import com.xinlian.member.server.controller.handler.WalletInfoHandler;
import com.xinlian.member.server.vo.WalletTradeOrderDetailVo;
import com.xinlian.member.server.vo.WalletTradeOrderDetailVoConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class UserBalanceServiceImpl  implements UserBalanceService  {
    @Autowired
    private AdminOptionsMapper adminOptionsMapper;
    @Autowired
    private TLockPositionMapper lockPositionMapper;
    @Autowired
    private UserBalanceMapper userBalanceMapper;
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TChainOwnerMapper chainOwnerMapper;
    @Autowired
    private TCurrencyManageMapper currencyManageMapper;
    @Autowired
    private WalletInfoHandler walletInfoHandler;
    @Autowired
    private WithdrawCustomerService withdrawCustomerService;
    @Autowired
    private TUserAuthMapper userAuthMapper;
    @Autowired
    private TUserMapper userMapper;
    @Value("${isTest}")
    private boolean isTest;

    @Override
    public ResponseResult getAllCurrencyBalance(Long userId,Long nodeId) {
        //用户锁仓金额
        BigDecimal amount = BigDecimal.ZERO;
        //是否展示锁仓金额 - 0：不展示，1：展示
        int amountStatus = 0;
        //获取锁仓usdt数量
        TLockPosition tLockPosition = new TLockPosition();
        tLockPosition.setUid(userId);
        TLockPosition lockPosition = lockPositionMapper.selectOne(tLockPosition);
        if (null != lockPosition) {
            if (null != lockPosition.getAmountCode() && CurrencyEnum.USDT.getCurrencyId() == lockPosition.getAmountCode()) {
                //如果锁仓的是USDT，需要换算成CAT
                BigDecimal usdtAmount = lockPosition.getUsdtAmount();
                //从配置项列表中获取CAT单价（暂定为36.3550不变）
                String lockPrice = null;
                lockPrice = getAdminOptionsValue("lockPrice");
                if (null == lockPrice || "".equals(lockPrice)){ lockPrice = "36.3550";}
                //换算锁仓金额
                amount = usdtAmount.divide(new BigDecimal(lockPrice), 4, BigDecimal.ROUND_DOWN);
            }
            if (null != lockPosition.getAmountCode() && CurrencyEnum.CAT.getCurrencyId() == lockPosition.getAmountCode()) {
                //如果锁仓的是CAT
                amount = lockPosition.getCatAmount();
            }
            amountStatus = 1;
        }
        //1查询用户所有币种余额
        //2根据各币种查询手续费，单价等属性
        //3算出总额返回
        List<AllCurrencyRes> list = userBalanceMapper.selectUserBalance(userId);
        boolean judgeCashUserSetFlag = withdrawCustomerService.checkWithdrawCustomerUid(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (AllCurrencyRes allCurrencyRes : list) {
            total= allCurrencyRes.getDollariteam().add(total);
            //写死，锁仓只出现在CAT币种下
            if (6 == allCurrencyRes.getCurrencyId()) {
                allCurrencyRes.setAmount(amount);
                allCurrencyRes.setAmountStatus(amountStatus);
            }
            if(judgeCashUserSetFlag && CurrencyEnum.USDT.getCurrencyId() == allCurrencyRes.getCurrencyId()){
                allCurrencyRes.setCash(CurrencyManagerCashEnum.CASH_YES.getCode());
            }
        }
        int chainOwnerStatus = 0;//是否是链权人 - 0：不是链权人，1：是链权人
        ChainOwnerAssetRes chainOwnerAssetRes = findChainOwnerAsset(userId);
        if (null != chainOwnerAssetRes){ chainOwnerStatus = 1;}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",total);
        jsonObject.put("coin_wallet",list);
        jsonObject.put("chainOwnerStatus",chainOwnerStatus);
        jsonObject.put("chainOwnerAssetRes",chainOwnerAssetRes);
        jsonObject.put("isCurrencyAddressFlag",0);
        //判断是否有maleChain地址
        if(!walletInfoHandler.checkCurrencyAddressStatus(userId)){
            jsonObject.put("isCurrencyAddressFlag",2);
        }
        //校验该账户已通过实名的身份证号下的所有账户中是否在Hi Cat有过登录
        //true：需要强制下载和登录Hi Cat，false：不需要
        if (isTest){
            jsonObject.put("isForceDownloadHiCat",false);
        }else {
            List<String> uidList = userAuthMapper.findUidListByAuthSn(userId);
            if (null == uidList || uidList.size() == 0) {
                //未实名的用户
                jsonObject.put("isForceDownloadHiCat",false);
            }else {
                jsonObject.put("isForceDownloadHiCat",false); //TODO
//                List<TUser> hiCatUserList = userMapper.findUserByUidList(uidList);
//                if (null == hiCatUserList || hiCatUserList.size() == 0) {
//                    jsonObject.put("isForceDownloadHiCat",true);
//                }else {
//                    jsonObject.put("isForceDownloadHiCat",false);
//                }
            }
        }
        return ResponseResult.builder().result(jsonObject).code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

    /**
     * 获取配置项列表中的optionValue值
     * @param optionsName 配置项name
     * @return 配置项value值
     */
    private String getAdminOptionsValue(String optionsName){
        String getValue = redisClient.get(optionsName);
        if(null==getValue){
            String optionsValue = adminOptionsMapper.getAdminOptionValueByKey(optionsName);
            if(null == optionsValue){return null;}
            redisClient.set(optionsName,optionsValue);
            return optionsValue;
        }
        return getValue;
    }

    /**
     * 查询链权人资产
     * @param uid 用户id
     * @return 链权人资产实体
     */
    private ChainOwnerAssetRes findChainOwnerAsset(Long uid) {
        TChainOwner tChainOwner = new TChainOwner();
        tChainOwner.setUid(uid);
        TChainOwner chainOwner = chainOwnerMapper.selectOne(tChainOwner);
        //根据uid到链权人表查找信息，作为判断是否为链权人的依据
        if (null == chainOwner){ return null;}
        ChainOwnerAssetRes chainOwnerAssetRes = new ChainOwnerAssetRes();//链权人资产实体
        chainOwnerAssetRes.setUrl(chainOwner.getUrl());
        chainOwnerAssetRes.setSignStatus(chainOwner.getSignStatus());
        chainOwnerAssetRes.setCatAmount(chainOwner.getCatAmount());
        //从币种管理中获取CAT实时市价
        TCurrencyManage tCurrencyManage = new TCurrencyManage();
        tCurrencyManage.setCurrencyId(6L);
        BigDecimal dollar = currencyManageMapper.selectOne(tCurrencyManage).getDollar();
        BigDecimal bigDecimal = dollar.multiply(chainOwner.getCatAmount()).setScale(4, BigDecimal.ROUND_DOWN);
        chainOwnerAssetRes.setCatValue(bigDecimal);
        return chainOwnerAssetRes;
    }

    //我的某个币种下交易记录接口
    @Override
    public PageInfo getTradeInfo(TradeInfoReq tradeInfoReq) {
        PageHelper.startPage(tradeInfoReq.getPage(), 10);
        return new PageInfo(tWalletTradeOrderMapper.getTradeInfo(tradeInfoReq));
    }

    @Override
    public ResponseResult getOneTradeInfo(GetOneTradeInfoReq getOneTradeInfoReq) {
        try {
            TWalletTradeOrder tWalletTradeOrder=tWalletTradeOrderMapper.getOneTradeInfo(getOneTradeInfoReq);
            WalletTradeOrderDetailVo walletTradeOrderDetailVo = new WalletTradeOrderDetailVoConvertor().convert(tWalletTradeOrder);
            return ResponseResult.builder().result(walletTradeOrderDetailVo).code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
        }catch (Exception e){
            log.error("出现异常,",e);
            throw new BizException("系统繁忙");
        }
    }

    @Override
    public ResponseResult getCurrencyBalanceInfo(CurrencyInfoRes currencyInfoRes) {
        CurrencyBalanceRes currencyBalanceRes=tWalletTradeOrderMapper.getCurrencyBalanceInfo(currencyInfoRes);
        return ResponseResult.builder().result(currencyBalanceRes).code(ErrorCode.REQ_SUCCESS.getCode()).msg(ErrorCode.REQ_SUCCESS.getDes()).build();
    }

}
