package com.xinlian.admin.server.controller.handler;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.service.SmartContractHistoryBillService;
import com.xinlian.biz.model.TSmartContractHistoryBill;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Song
 * @date 2020-06-22 13:52
 * @description 智能合约统计
 */
@Configuration
@EnableScheduling
@Component
@Slf4j
public class StatisticSmartContractHandler {

    @Autowired
    private SmartContractHistoryBillService smartContractHistoryBillService;

    private static ThreadLocal<Integer> tryNumber = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * 每天凌晨1分执行
     * 执行智能合约统计定时任务
     */
    @Scheduled(cron = "59 59 23 * * ?")
    public void doStatisticsTask(){
        try {
            log.error("~~~~~~~~~~~~~~~~~执行智能合约统计定时任务start");
            statisticsBillDate(DateFormatUtil.get(9,new Date()));
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"执行智能合约定时任务发现异常:{}",e.toString(),e);
            int exceptionTryNumber = tryNumber.get().intValue();
            if(exceptionTryNumber<1) { //尝试一次
                exceptionTryNumber++;
                doStatisticsTask();
            }
        }
    }

    /**
     * 根据账期补偿
     * @param billDate 账期
     */
    public void statisticsBillDate(String billDate)throws Exception{
        String billDateEnd = new StringBuffer(billDate).append(" 23:59:59").toString();
        String billDateStart = new StringBuffer(billDate).append(" 00:00:00").toString();
        Long endTime = DateFormatUtil.dateToLong(billDateEnd,DateFormatUtil.formatDateStyle);
        Long startTime = DateFormatUtil.dateToLong(billDateStart,DateFormatUtil.formatDateStyle);
        //1.统计钱包现存总额与节点现存币种对应金额
        TSmartContractHistoryBill lastHistoryBill = smartContractHistoryBillService.getByBillDate(DateFormatUtil.getSubOneDate(billDate));
        //2.已完成智能合约订单数据
        TSmartContractHistoryBill todayBillData = smartContractHistoryBillService.completeSmartContractOrders(startTime,endTime);
        //3.组装数据
        TSmartContractHistoryBill assemblyBillData = this.convertBillData(billDate,lastHistoryBill,todayBillData);
        smartContractHistoryBillService.insert(assemblyBillData);
    }


    /**
     * 组装账单数据参数
     * @param billDate
     * @param lastHistoryBill
     * @param todayBillData
     * @return
     * @throws Exception
     */
    private TSmartContractHistoryBill convertBillData(String billDate,TSmartContractHistoryBill lastHistoryBill, TSmartContractHistoryBill todayBillData)throws Exception {
        log.error("账期："+billDate);
        log.error("lastHistoryBill：" + JSONObject.toJSONString(lastHistoryBill));
        log.error("todayBillData：" + JSONObject.toJSONString(todayBillData));
        if(null==lastHistoryBill){
            lastHistoryBill = new TSmartContractHistoryBill();
        }
        if(null==todayBillData){
            todayBillData = new TSmartContractHistoryBill();
        }
        TSmartContractHistoryBill assemblyBillData = new TSmartContractHistoryBill();
        //期初 = 上期期末值
        assemblyBillData.setBillDate(DateFormatUtil.parseDateStr(billDate));
        assemblyBillData.setSellerInitialInAmount(null==lastHistoryBill.getSellerEndInAmount()?UdunBigDecimalUtil.zeroBigDecimal:lastHistoryBill.getSellerEndInAmount());
        assemblyBillData.setSellerInitialOutAmount(null==lastHistoryBill.getSellerEndOutAmount()?UdunBigDecimalUtil.zeroBigDecimal:lastHistoryBill.getSellerEndOutAmount());
        assemblyBillData.setBuyerInitialOutAmount(null==lastHistoryBill.getBuyerEndOutAmount()?UdunBigDecimalUtil.zeroBigDecimal:lastHistoryBill.getBuyerEndOutAmount());
        assemblyBillData.setBuyerInitialInAmount(null==lastHistoryBill.getBuyerEndInAmount()?UdunBigDecimalUtil.zeroBigDecimal:lastHistoryBill.getBuyerEndInAmount());
        //当日
        assemblyBillData.setSellerTodayInAmount(null==todayBillData.getSellerTodayInAmount()?UdunBigDecimalUtil.zeroBigDecimal:todayBillData.getSellerTodayInAmount());
        assemblyBillData.setSellerTodayOutAmount(UdunBigDecimalUtil.convertMinusJudgeZero(todayBillData.getSellerTodayOutAmount()));
        assemblyBillData.setBuyerTodayInAmount(null==todayBillData.getBuyerTodayInAmount()?UdunBigDecimalUtil.zeroBigDecimal:todayBillData.getBuyerTodayInAmount());
        assemblyBillData.setBuyerTodayOutAmount(UdunBigDecimalUtil.convertMinusJudgeZero(todayBillData.getBuyerTodayOutAmount()));
        //期末
        BigDecimal sellerEndInAmount = UdunBigDecimalUtil.statisticAddNum(lastHistoryBill.getSellerEndInAmount(),todayBillData.getSellerTodayInAmount());
        assemblyBillData.setSellerEndInAmount(sellerEndInAmount);
        BigDecimal sellerEndOutAmount =  UdunBigDecimalUtil.statisticAddNum(assemblyBillData.getSellerInitialOutAmount(),assemblyBillData.getSellerTodayOutAmount());
        assemblyBillData.setSellerEndOutAmount(UdunBigDecimalUtil.convertMinusJudgeZero(sellerEndOutAmount));
        BigDecimal buyerEndInAmount = UdunBigDecimalUtil.statisticAddNum(lastHistoryBill.getBuyerEndInAmount(),assemblyBillData.getBuyerTodayInAmount());
        assemblyBillData.setBuyerEndInAmount(buyerEndInAmount);
        BigDecimal buyerEndOutAmount = UdunBigDecimalUtil.statisticAddNum(lastHistoryBill.getBuyerEndOutAmount(),assemblyBillData.getBuyerTodayOutAmount());
        assemblyBillData.setBuyerEndOutAmount(UdunBigDecimalUtil.convertMinusJudgeZero(buyerEndOutAmount));
        assemblyBillData.setCatOutInDiffAmount(this.calculationEndCatOutInDiffAmount(assemblyBillData));
        assemblyBillData.setUsdtOutInDiffAmount(this.calculationEndUsdtOutInDiffAmount(assemblyBillData));
        assemblyBillData.setBillStatus(2);
        if(assemblyBillData.getCatOutInDiffAmount().compareTo(UdunBigDecimalUtil.zeroBigDecimal)==0
                && assemblyBillData.getUsdtOutInDiffAmount().compareTo(UdunBigDecimalUtil.zeroBigDecimal)==0){
            assemblyBillData.setBillStatus(1);
        }
        assemblyBillData.setCreateTime(new Date());
        return assemblyBillData;
    }

    private BigDecimal calculationEndCatOutInDiffAmount(TSmartContractHistoryBill assemblyBillData){
        return UdunBigDecimalUtil.arrayBigDecimalAdd(assemblyBillData.getSellerEndOutAmount(),assemblyBillData.getSellerInitialOutAmount(),assemblyBillData.getSellerTodayOutAmount(),
                assemblyBillData.getBuyerEndInAmount(),assemblyBillData.getBuyerInitialInAmount(),assemblyBillData.getBuyerTodayInAmount());
    }

    private BigDecimal calculationEndUsdtOutInDiffAmount(TSmartContractHistoryBill assemblyBillData){
        return UdunBigDecimalUtil.arrayBigDecimalAdd(assemblyBillData.getBuyerEndOutAmount(),assemblyBillData.getBuyerInitialOutAmount(),assemblyBillData.getBuyerTodayOutAmount(),
                assemblyBillData.getSellerEndInAmount(),assemblyBillData.getSellerInitialInAmount(),assemblyBillData.getSellerTodayInAmount());
    }
}
