package com.xinlian.admin.biz.service;

import com.xinlian.biz.dao.CurrencyBalanceHourChangeMapper;
import com.xinlian.common.enums.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Song
 * @date 2020-07-25 14:14
 * @description 币种余额每小时变化
 */
@Slf4j
@Service
public class CurrencyBalanceHourChangeService {

    @Autowired
    private CurrencyBalanceHourChangeMapper currencyBalanceHourChangeMapper;
    /**
     * 统计币种余额到新表中
     */
    public void statisticsCurrencyBalance() {
        long startTime = System.currentTimeMillis();
        //执行insert into select from 语句
        Executor executor = Executors.newFixedThreadPool(6);
        CompletableFuture.supplyAsync(()->statisticsCurrencyBalance(CurrencyEnum.USDT.getCurrencyCode(),CurrencyEnum.USDT.getCurrencyId()),executor);
        CompletableFuture.supplyAsync(()->statisticsCurrencyBalance(CurrencyEnum.CAT.getCurrencyCode(),CurrencyEnum.CAT.getCurrencyId()),executor);
        CompletableFuture.supplyAsync(()->statisticsCurrencyBalance(CurrencyEnum.CAG.getCurrencyCode(),CurrencyEnum.CAG.getCurrencyId()),executor);
        CompletableFuture.supplyAsync(()->statisticsCurrencyBalance(CurrencyEnum.GPT.getCurrencyCode(),CurrencyEnum.GPT.getCurrencyId()),executor);
        long endTime = System.currentTimeMillis();
        log.info("并行结束时间:" + (endTime-startTime));
    }

    private String statisticsCurrencyBalance(String currencyCode, int currencyId){
        return currencyBalanceHourChangeMapper.statisticsCurrencyBalance(currencyCode,currencyId)==null?"":"1";
    }

}
