package com.xinlian.admin.server.controller.handler;

import com.xinlian.admin.biz.service.AccountCheckService;
import com.xinlian.common.utils.DateFormatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 财务核查
 * 统计每天出入数据
 */
@Configuration
@EnableScheduling
@Component
public class StatisticsCurrencyEveryDayDataPlugTaskHandler {


    @Autowired
    private AccountCheckService accountCheckService;

    /**
     * 执行定时任务
     */
    //@Scheduled(cron = "0/2 * * * * ? ")
    @Scheduled(cron = "0 0 0 * * ? ")
    public void doStatisticsTask(){
        Date clearDate = DateFormatUtil.addDate(new Date(),-1);
        String clearDay = DateFormatUtil.get(7,clearDate);
        //1.统计钱包现存总额与节点现存币种对应金额
        accountCheckService.staticWalletInfo(clearDay);
    }

    /**
     * 根据统计币种公链来往数据，更新财务核查表
     */
    @Scheduled(cron = "0 10 0 * * ? ")
    public void updateAccountCheckData(){
        Date clearDate = DateFormatUtil.addDate(new Date(),-1);
        String clearDay = DateFormatUtil.get(7,clearDate);
        this.doScheduledUpdateTradeOrder(clearDay);
    }



    public void doScheduledUpdateTradeOrder(String clearDay){
        //2.统计各节点站外充值
        accountCheckService.staticOffSiteRechargeNum(clearDay);
        //3.统计各节点站外提现
        accountCheckService.staticOffSiteWithdraw(clearDay);
        //4.统计各节点火箭到钱包
        accountCheckService.staticFromRocketNum(clearDay);
        //5.统计各节点钱包火箭
        accountCheckService.staticToRocketNum(clearDay);
        //6.其他入账
        accountCheckService.staticOtherRecorded(clearDay);
        //7.其他出账
        accountCheckService.staticOtherChargeOff(clearDay);
    }

    /**
     * 把数据汇总到顶级节点下
     */
    @Scheduled(cron = "0 5 1 * * ? ")
    public void summaryTopNodeDataTask(){
        Date clearDate = DateFormatUtil.addDate(new Date(),-1);
        String clearDay = DateFormatUtil.get(7,clearDate);
        this.summaryTopNodeData(clearDay);
    }

    /**
     * 留个入口补偿执行
     * @param clearDay
     */
    public void summaryTopNodeData(String clearDay) {
        //数据汇总到顶级节点下 - 方便分页查询
        accountCheckService.summaryTopNodeData(clearDay);
    }
}
