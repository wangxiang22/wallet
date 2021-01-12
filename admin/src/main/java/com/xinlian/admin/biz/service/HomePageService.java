package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.server.vo.response.IndexInfoDataResponse;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.utils.PrStringUtils;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * com.xinlian.admin.biz.service
 *
 * @date 2020/2/17 13:48
 */
@Service
@Slf4j
public class HomePageService {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;

    private final Long indexLoseEfficacyTimesNum = 2*60*60L;
    /**
     * 获取首页统计数据
     * @param isForceRefresh 是否强制刷新
     * @return
     */
    public IndexInfoDataResponse getIndexData(boolean isForceRefresh) throws Exception{
        IndexInfoDataResponse infoDataResponse = new IndexInfoDataResponse();
        //累计注册总数 -- 统计所有uid
        CompletableFuture<String> grandTotalRegisterValue =  CompletableFuture.supplyAsync(()->queryGrandTotalRegisterValue(isForceRefresh));
        //累计自然用户总数 - 一个身份证号码算一个
        CompletableFuture<String> grandTotalIdNosValue =  CompletableFuture.supplyAsync(()->queryGrandTotalIdNosValue(isForceRefresh));
        //所有已激活矿机用户总数
        CompletableFuture<String> activateTotalValue =  CompletableFuture.supplyAsync(()->queryActivateTotalValue(isForceRefresh));
        //今日新增注册总数
        CompletableFuture<String> todayGrandRegisterValue = CompletableFuture.supplyAsync(()->queryTodayGrandRegisterValue(isForceRefresh));
        //今日自然用户总数
        CompletableFuture<String> todayGrandIdNosValue = CompletableFuture.supplyAsync(()->queryTodayGrandIdNosValue(isForceRefresh));
        //今日已激活矿机用户总数
        CompletableFuture<String> todayActivateValue = CompletableFuture.supplyAsync(()->queryTodayActivateValue(isForceRefresh));
        //获取值
        infoDataResponse.setGrandTotalRegisterValue(grandTotalRegisterValue.get());
        infoDataResponse.setGrandTotalIdNosValue(grandTotalIdNosValue.get());
        infoDataResponse.setActivateTotalValue(activateTotalValue.get());
        infoDataResponse.setTodayGrandRegisterValue(todayGrandRegisterValue.get());
        infoDataResponse.setTodayGrandIdNosValue(todayGrandIdNosValue.get());
        infoDataResponse.setTodayActivateValue(todayActivateValue.get());
        return infoDataResponse;
    }

    /**
     * 当天 已激活矿机客户总数
     * @param isForceRefresh
     * @return
     */
    private String queryTodayActivateValue(boolean isForceRefresh) {
        String activateTotalKey = "ADMIN_TODAY_ACTIVATE_TOTAL_VALUE_KEY";
        String activateTotalValue = redisClient.get(activateTotalKey);
        if(isForceRefresh || null==activateTotalValue){
            Long queryActivateTotalValue = userInfoMapper.queryActivateTotalValue("today");
            activateTotalValue = PrStringUtils.fmtNumToString(queryActivateTotalValue);
            redisClient.set(activateTotalKey,activateTotalValue,indexLoseEfficacyTimesNum);
        }
        return activateTotalValue;
    }

    private String queryTodayGrandIdNosValue(boolean isForceRefresh) {
        String totalIdNosKey = "ADMIN_TOADY_GRAND_TOTAL_IDNO_VALUE_KEY";
        String grandTotalIdNosValue = redisClient.get(totalIdNosKey);
        if(isForceRefresh || null==grandTotalIdNosValue){
            Long queryTotalIdNosValue = userInfoMapper.queryGrandTotalIdNosValue("today");
            grandTotalIdNosValue = PrStringUtils.fmtNumToString(queryTotalIdNosValue);
            redisClient.set(totalIdNosKey,grandTotalIdNosValue,indexLoseEfficacyTimesNum);
        }
        return grandTotalIdNosValue;
    }

    private String queryTodayGrandRegisterValue(boolean isForceRefresh) {
        String registerKey = "ADMIN_TODAY_GRAND_TOTAL_REGISTER_VALUE_KEY";
        String grandTotalRegister = redisClient.get(registerKey);
        if(isForceRefresh || null==grandTotalRegister){
            Long queryTotalRegisterValue = userInfoMapper.queryGrandTotalRegisterValue("today");
            grandTotalRegister = PrStringUtils.fmtNumToString(queryTotalRegisterValue);
            redisClient.set(registerKey,grandTotalRegister,indexLoseEfficacyTimesNum);
        }
        return grandTotalRegister;
    }

    /**
     * 所有已激活矿机客户总数
     * @param isForceRefresh
     * @return
     */
    private String queryActivateTotalValue(boolean isForceRefresh) {
        String activateTotalKey = "ADMIN_ACTIVATE_TOTAL_VALUE_KEY";
        String activateTotalValue = redisClient.get(activateTotalKey);
        if(isForceRefresh || null==activateTotalValue){
            Long queryActivateTotalValue = userInfoMapper.queryActivateTotalValue(null);
            activateTotalValue = PrStringUtils.fmtNumToString(queryActivateTotalValue);
            redisClient.set(activateTotalKey,activateTotalValue,indexLoseEfficacyTimesNum);
        }
        return activateTotalValue;
    }

    /**
     * 累计自然用户总数
     * @param isForceRefresh 是否强制刷新
     * @return
     */
    private String queryGrandTotalIdNosValue(boolean isForceRefresh) {
        String totalIdNosKey = "ADMIN_GRAND_TOTAL_IDNO_VALUE_KEY";
        String grandTotalIdNosValue = redisClient.get(totalIdNosKey);
        if(isForceRefresh || null==grandTotalIdNosValue){
            Long queryTotalIdNosValue = userInfoMapper.queryGrandTotalIdNosValue(null);
            grandTotalIdNosValue = PrStringUtils.fmtNumToString(queryTotalIdNosValue);
            redisClient.set(totalIdNosKey,grandTotalIdNosValue,indexLoseEfficacyTimesNum);
        }
        return grandTotalIdNosValue;
    }

    /**
     * 累计注册总数
     * @param isForceRefresh 是否强制刷新
     * @return
     */
    private String queryGrandTotalRegisterValue(boolean isForceRefresh) {
        String registerKey = "ADMIN_GRAND_TOTAL_REGISTER_VALUE_KEY";
        String grandTotalRegister = redisClient.get(registerKey);
        if(isForceRefresh || null==grandTotalRegister){
            Long queryTotalRegisterValue = userInfoMapper.queryGrandTotalRegisterValue(null);
            grandTotalRegister = PrStringUtils.fmtNumToString(queryTotalRegisterValue);
            redisClient.set(registerKey,grandTotalRegister,indexLoseEfficacyTimesNum);
        }
        return grandTotalRegister;
    }

    /**
     * 获取钱包金额数据
     * @param isForceRefresh 是否强制更新
     * @param currencyCode 币种code USDT
     * @return
     */
    public Map<String,String> getPlatformWalletData(boolean isForceRefresh, String currencyCode) {
        Map<String,String> map = getTotalWalletData(isForceRefresh,currencyCode);
        String todayTotalWalletDataByCurrencyCode = getTodayTotalWalletData(isForceRefresh,currencyCode);
        map.put("today",todayTotalWalletDataByCurrencyCode);
        return map;
    }

    /**
     * 获取钱包金额数据 -- 所有币种  - 数据核查
     * @return
     */
    public Map<String,String> getStatisticsCurrencyNum() throws Exception{
        Executor executor = Executors.newFixedThreadPool(6);
        Map<String,String> map = new HashMap<String,String>();
        CompletableFuture<Map> future1 =  CompletableFuture.supplyAsync(()->getTotalWalletData(true,CurrencyEnum.USDT.getCurrencyCode()),executor);
        CompletableFuture<Map> future2 =  CompletableFuture.supplyAsync(()->getTotalWalletData(true,CurrencyEnum.CAG.getCurrencyCode()),executor);
        CompletableFuture<Map> future3 =  CompletableFuture.supplyAsync(()->getTotalWalletData(true,CurrencyEnum.CAT.getCurrencyCode()),executor);
        CompletableFuture<Map> future4 =  CompletableFuture.supplyAsync(()->getTotalWalletData(true,CurrencyEnum.GPT.getCurrencyCode()),executor);
        CompletableFuture<Map> future5 =  CompletableFuture.supplyAsync(()->getTotalWalletData(true,CurrencyEnum.ETH.getCurrencyCode()),executor);
        map.put(CurrencyEnum.USDT.getCurrencyCode(),future1.get().get("total").toString());
        map.put(CurrencyEnum.CAG.getCurrencyCode(),future2.get().get("total").toString());
        map.put(CurrencyEnum.CAT.getCurrencyCode(),future3.get().get("total").toString());
        map.put(CurrencyEnum.GPT.getCurrencyCode(),future4.get().get("total").toString());
        map.put(CurrencyEnum.ETH.getCurrencyCode(),future5.get().get("total").toString());
        return map;
    }

    private String getTodayTotalWalletData(boolean isForceRefresh, String currencyCode) {
        String todayWalletDataKey = "ADMIN_TODAY_TOTAL_WALLET_DATA_" + currencyCode + "KEY";
        String todayWalletDataValue = redisClient.get(todayWalletDataKey);
        if(isForceRefresh || null==todayWalletDataValue) {
            //package static param sql query
            BigDecimal todayWallet = walletTradeOrderMapper.getTodayWalletData(currencyCode);
            todayWalletDataValue = PrStringUtils.fmtNumToString(todayWallet);
            redisClient.set(todayWalletDataKey,todayWalletDataValue,indexLoseEfficacyTimesNum);
        }
        return todayWalletDataValue;
    }

    private Map<String,String> getTotalWalletData(boolean isForceRefresh, String currencyCode){
        String totalWalletDataKey =  "ADMIN_TOTAL_WALLET_DATA_"+currencyCode+"_TOTAL_KEY";
        String totalBalanceWalletDataKey = "ADMIN_TOTAL_WALLET_DATA_"+currencyCode+"_BALANCE_KEY";
        String totalFrozenWalletDataKey = "ADMIN_TOTAL_WALLET_DATA_"+currencyCode+"_FROZEN_KEY";
        String currencyTotal = redisClient.get(totalWalletDataKey);
        String balanceTotal = redisClient.get(totalBalanceWalletDataKey);
        String freezeTotal = redisClient.get(totalFrozenWalletDataKey);
        if(isForceRefresh || null==balanceTotal || null == currencyTotal){
            Map currencyMap = walletInfoMapper.queryTotalWalletData(currencyCode);
            BigDecimal balanceNumber = new BigDecimal(currencyMap.get("balanceTotal").toString());
            BigDecimal frozenNumber = new BigDecimal(currencyMap.get("freezeTotal").toString());
            BigDecimal currencyTotalBigDecimal = UdunBigDecimalUtil.addNum(balanceNumber,frozenNumber);
            balanceTotal = PrStringUtils.fmtNumToString(balanceNumber);
            freezeTotal = PrStringUtils.fmtNumToString(frozenNumber);
            currencyTotal = PrStringUtils.fmtNumToString(currencyTotalBigDecimal);
            redisClient.set(totalWalletDataKey,currencyTotal,indexLoseEfficacyTimesNum);
            redisClient.set(totalBalanceWalletDataKey,balanceTotal,indexLoseEfficacyTimesNum);
            redisClient.set(totalFrozenWalletDataKey,freezeTotal,indexLoseEfficacyTimesNum);
        }
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("total",currencyTotal);
        resultMap.put("balanceTotal",balanceTotal);
        resultMap.put("freezeTotal",freezeTotal);
        return resultMap;
    }

    /**
     *     //累计注册总数 -- 所有的统计uid
     *     private String grandTotalRegisterValue;
     *     //累计自然用户总数  -- 一个身份证号码算一个
     *     private String grandTotalIdNosValue;
     *     //所有已激活矿机用户总数
     *     private String activateTotalValue;
     *     //今日新增注册总数
     *     private String todayGrandRegisterValue;
     *     //今日自然用户总数  -- 一个身份证号码算一个
     *     private String todayGrandIdNosValue;
     *     //今日已激活矿机用户总数
     *     private String todayActivateValue;
     */
}
