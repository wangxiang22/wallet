package com.xinlian.admin.server.controller.handler;

import com.xinlian.admin.biz.service.CurrencyBalanceHourChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * com.xinlian.admin.server.controller.handler
 * 统计币种余额变化  - 每小时变化
 * @author by Song
 * @date 2020/6/23 23:15
 */
@Configuration
@EnableScheduling
@Component
@Slf4j
public class StatisticsCurrencyBalanceHandler {

    @Autowired
    private CurrencyBalanceHourChangeService currencyBalanceHourChangeService;

    @Scheduled(cron = "0 0 0/1 * * ? ")
    public void statisticCurrencyBalance(){
        currencyBalanceHourChangeService.statisticsCurrencyBalance();
    }


}
