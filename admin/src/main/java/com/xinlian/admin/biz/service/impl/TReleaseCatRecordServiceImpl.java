package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.service.TReleaseCatRecordService;
import com.xinlian.biz.dao.TChainOwnerMapper;
import com.xinlian.biz.dao.TReleaseCatRecordMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.MiddleModel;
import com.xinlian.biz.model.TReleaseCatRecord;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 释放cat记录表 服务实现类
 * </p>
 *
 * @author 插件生成
 * @since 2020-01-13
 */
@Configuration
@EnableScheduling
@Service
@Slf4j
public class TReleaseCatRecordServiceImpl implements TReleaseCatRecordService {

    @Autowired
    private TReleaseCatRecordMapper releaseCatRecordMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper; //增加钱包cat值
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;//增加cat，需要增加对应流水值
    @Autowired
    private TChainOwnerMapper chainOwnerMapper;

    @Override
    public void batchInsert(Map<String,Object> map) {
        List<TReleaseCatRecord> list = (List)map.get("insertList");
        releaseCatRecordMapper.batchInsert(list);
    }

    @Scheduled(cron = "0 0/2 * * * ?")
    @Override
    @Transactional
    public void timingTaskReleaseCatRecord() {
        //获取待解仓信息集合
        List<TReleaseCatRecord> lists = getReleaseCatRecordList(null);
        if(null==lists || 0 == lists.size() ){ return;}
        doTimingTaskReleaseCatRecord(lists, null);
    }


    //1.获取待处理链权人cat集合
    public List<TReleaseCatRecord> getReleaseCatRecordList(BigDecimal groupByReleaseCatNum){
        List<TReleaseCatRecord> releaseCatRecordList = releaseCatRecordMapper.getWaitReleaseCatRecord(groupByReleaseCatNum);
        if(releaseCatRecordList.size()==0 || null==releaseCatRecordList){ return null;}
        //修改 待处理链权人集合 状态 待处理 - 处理中
        releaseCatRecordMapper.batchUpdateToProcessing(releaseCatRecordList,1,2);
        return releaseCatRecordList;
    }

    public void doTimingTaskReleaseCatRecord(List<TReleaseCatRecord> lists,BigDecimal releaseCatNum){
        if(null==releaseCatNum){
            releaseCatNum = lists.get(0).getReleaseCatNum();
        }
        batchDisposeSubtractLock(lists,releaseCatNum);//释放CAT锁仓
        batchDisposeWalletInfo(lists,releaseCatNum);
    }

    //减锁定链权表数值
    public int batchDisposeSubtractLock(List<TReleaseCatRecord> releaseCatRecordList,BigDecimal groupByReleaseCatNum){
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("groupByReleaseCatNum",groupByReleaseCatNum);
        paramMap.put("releaseCatRecordList",releaseCatRecordList);
        int updateNum = chainOwnerMapper.batchDisposeSubtractLock(paramMap);
        if(releaseCatRecordList.size() != updateNum){
            throw new BizException("减锁定链权表数值出现异常!");
        }
        return updateNum;
    }

    //增加链权人对应钱包cat记录
    public int batchDisposeWalletInfo(List<TReleaseCatRecord> releaseCatRecordList,BigDecimal groupByReleaseCatNum){
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("groupByReleaseCatNum",groupByReleaseCatNum);
        paramMap.put("releaseCatRecordList",releaseCatRecordList);
        paramMap.put("currencyId", CurrencyEnum.CAT.getCurrencyId());
        int updateNum = walletInfoMapper.batchChainOwnerAddCat(paramMap);
        List<TWalletTradeOrder> lists = convertToWalletTradeRecord(paramMap);
        int tradeRecordNum = walletTradeOrderMapper.batchChainOwnerTradeRecord(lists);
        if(releaseCatRecordList.size() != updateNum || releaseCatRecordList.size() != tradeRecordNum ){
            throw new BizException("减锁定链权表数值出现异常!");
        }else{
            releaseCatRecordMapper.batchUpdateToProcessing(releaseCatRecordList,2,3);
        }
        return updateNum;
    }

    public List<TWalletTradeOrder> convertToWalletTradeRecord(Map<String, Object> paramMap) {
        List<TWalletTradeOrder> returnList = new ArrayList<TWalletTradeOrder>();
        List<TReleaseCatRecord> list = (List)paramMap.get("releaseCatRecordList");
        list.forEach(releaseCatRecord->{
            TWalletTradeOrder tradeOrder = new TWalletTradeOrder();
            tradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode());
            tradeOrder.setUid(releaseCatRecord.getUid());
            tradeOrder.setTradeCurrencyNum(releaseCatRecord.getReleaseCatNum());
            tradeOrder.setCurrencyId(Long.parseLong(CurrencyEnum.CAT.getCurrencyId()+""));
            tradeOrder.setCurrencyCode(CurrencyEnum.CAT.getCurrencyCode());
            tradeOrder.setTradeType(WalletTradeTypeEnum.RELEASE_CAT.getTradeType());
            tradeOrder.setDes(WalletTradeTypeEnum.RELEASE_CAT.getTradeDesc());
            returnList.add(tradeOrder);
        });
        return returnList;
    }


    @Override
    @Transactional
    //@Scheduled(cron = "0/1 * * * * ?")
    public void transactionDispose() {
        MiddleModel middleModel = releaseCatRecordMapper.getWaitDisposeMiddleSet();
        if(null==middleModel){return;}
        log.info("获取待处理信息--:{}", JSONObject.toJSONString(middleModel));
        updateMiddleStatusToDispose(middleModel);
        //1.删除交易记录 - orderId -
        int resultNum = releaseCatRecordMapper.deleteByKeyOrderId(middleModel.getOrderId());
        //2.减去钱包值 -- 单个执行
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setUid(middleModel.getUid());
        walletInfo.setCurrencyCode(CurrencyEnum.USDT.getCurrencyCode());

        middleModel.setTradeSum(UdunBigDecimalUtil.convertPlus(middleModel.getTradeSum()));

        int resultCount = releaseCatRecordMapper.updateWalletInfoBalanceNum(middleModel.getUid(),middleModel.getTradeSum());
        int resultStatusNum = updateMiddleStatusToComplete(middleModel);
        if(resultNum==0||resultCount==0||resultStatusNum==0){
            updateMiddleStatusToError(middleModel);
            throw new BizException("更新出现数据不匹配");
        }
    }




    public int updateMiddleStatusToDispose(MiddleModel middleModel) {
        return releaseCatRecordMapper.updateMiddleStatus(middleModel.getOrderId(),1,2);
    }

    public int updateMiddleStatusToComplete(MiddleModel middleModel) {
        return releaseCatRecordMapper.updateMiddleStatus(middleModel.getOrderId(),2,3);
    }

    public int updateMiddleStatusToError(MiddleModel middleModel) {
        return releaseCatRecordMapper.updateMiddleStatus(middleModel.getOrderId(),2,4);
    }
}
