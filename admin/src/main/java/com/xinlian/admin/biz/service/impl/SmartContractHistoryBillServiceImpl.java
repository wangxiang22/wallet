package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.service.SmartContractHistoryBillService;
import com.xinlian.biz.dao.TOrderMapper;
import com.xinlian.biz.dao.TSmartContractHistoryBillMapper;
import com.xinlian.biz.model.TOrder;
import com.xinlian.biz.model.TSmartContractHistoryBill;
import com.xinlian.common.dto.SmartContractTotalDto;
import com.xinlian.common.request.SmartContractHistoryBillPageReq;
import com.xinlian.common.response.*;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 智能合约历史账单管理 服务实现类
 * </p>
 *
 * @author lt
 * @since 2020-06-18
 */
@Slf4j
@Service
public class SmartContractHistoryBillServiceImpl extends ServiceImpl<TSmartContractHistoryBillMapper, TSmartContractHistoryBill> implements SmartContractHistoryBillService {

    @Autowired
    private TSmartContractHistoryBillMapper smartContractHistoryBillMapper;
    @Autowired
    private TOrderMapper orderMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public TSmartContractHistoryBill getByBillDate(String billDate) {
        return smartContractHistoryBillMapper.getByBillDate(billDate);
    }

    @Override
    public TSmartContractHistoryBill completeSmartContractOrders(Long startTime, Long endTime) {
        return orderMapper.completeSmartContractOrders(startTime,endTime);
    }

    @Override
    public PageResult<List<SmartContractHistoryBillRes>> findHistoryBillPage(SmartContractHistoryBillPageReq req) {
        if (null != req.getEndBillDate() && !"".equals(req.getEndBillDate())) {
            try {
                req.setEndBillDate(DateFormatUtil.getAddOneDate(req.getEndBillDate()));
            } catch (ParseException e) {
                log.error("智能合约历史账单参数日期加一天出现异常：{}",e.toString(),e);
            }
        }
        PageResult<List<SmartContractHistoryBillRes>> result = new PageResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        PageHelper.startPage(req.getPageNum(),req.getPageSize());
        List<TSmartContractHistoryBill> historyBillList = smartContractHistoryBillMapper.findHistoryBillPage(req);
        List<SmartContractHistoryBillRes> historyBillResList = new ArrayList<>();
        if (null != historyBillList && historyBillList.size() > 0) {
            historyBillList.forEach(TSmartContractHistoryBill -> historyBillResList.add(TSmartContractHistoryBill.historyBillRes()));
            PageInfo<TSmartContractHistoryBill> pageInfo = new PageInfo<>(historyBillList);
            result.setCurPage(pageInfo.getPageNum());
            result.setPageSize(pageInfo.getPageSize());
            result.setTotal(pageInfo.getTotal());
            result.setResult(historyBillResList);
        }
        return result;
    }

    @Override
    public ResponseResult<List<SmartContractHistoryBillRes>> findHistoryBill(SmartContractHistoryBillPageReq req) {
        if (null != req.getEndBillDate() && !"".equals(req.getEndBillDate())) {
            try {
                req.setEndBillDate(DateFormatUtil.getAddOneDate(req.getEndBillDate()));
            } catch (ParseException e) {
                log.error("智能合约历史账单参数日期加一天出现异常：{}",e.toString(),e);
            }
        }
        ResponseResult<List<SmartContractHistoryBillRes>> result = new ResponseResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        List<TSmartContractHistoryBill> historyBillList = smartContractHistoryBillMapper.findHistoryBillPage(req);
        List<SmartContractHistoryBillRes> historyBillResList = new ArrayList<>();
        if (null != historyBillList && historyBillList.size() > 0) {
            historyBillList.forEach(TSmartContractHistoryBill -> historyBillResList.add(TSmartContractHistoryBill.historyBillRes()));
            result.setResult(historyBillResList);
        }
        return result;
    }

    @Override
    public ResponseResult<SmartContractTotalRes> findTotalOutInAmount() {
        ResponseResult<SmartContractTotalRes> result = new ResponseResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SmartContractTotalDto totalDto = smartContractHistoryBillMapper.findSellerBuyerTotalAmount(today);
        if (null == totalDto) {
            SmartContractTotalRes smartContractTotalResZero = new SmartContractTotalRes();
            smartContractTotalResZero.setSellerTotalOutAmount("0.0000");
            smartContractTotalResZero.setSellerTotalInAmount("+0.0000");
            smartContractTotalResZero.setBuyerTotalOutAmount("0.0000");
            smartContractTotalResZero.setBuyerTotalInAmount("+0.0000");
            smartContractTotalResZero.setCatMargin("0.0000");
            smartContractTotalResZero.setUsdtMargin("0.0000");
            result.setResult(smartContractTotalResZero);
        }else {
            result.setResult(totalDto.smartContractTotalRes());
        }
        return result;
    }
    private final Long indexLoseEfficacyTimesNum = 2*60*60L;
    /**
     * 智能合约数据分析
     * @param isForceRefresh
     * @param dimensionsType
     * @return
     */
    @Override
    public Object dataAnalysis(boolean isForceRefresh, String dimensionsType){
        //从redis取值
        String redisKey = "ADMIN_SMART_CONTRACT_DATA_ANALYSIS_"+dimensionsType+"_KEY";
        String redisValue = redisClient.get(redisKey);
        if(isForceRefresh || null==redisValue){
            String lastDayOfWeekTimeStr = CommonUtil.getLastDayOfWeek(new Date());
            String firstDayOfWeekTimeStr = CommonUtil.getFirstDayOfWeek(new Date());
            Map<String, String[]> resultMap = new HashMap<>();
            if(!"HOUR".equals(dimensionsType)) {
                //调用某个执行sql
                List<SmartContractHisBillResponse> lists =
                        smartContractHistoryBillMapper.statisticsSmartContractHistBill(dimensionsType, firstDayOfWeekTimeStr, lastDayOfWeekTimeStr);
                //补数
                resultMap = complementDisposeData(lists, dimensionsType);
            }else {
                //查实时
                String billDate = DateFormatUtil.get(9, new Date());
                String billDateStart = new StringBuffer(billDate).append(" 00:00:00").toString();
                try {
                    Long startTime = DateFormatUtil.dateToLong(billDateStart, DateFormatUtil.formatDateStyle);
                    Long endTime = System.currentTimeMillis();
                    List<SmartContractHisBillResponse> lists =
                            orderMapper.smartContractUsdtPriceInTime(startTime,endTime);
                    resultMap = complementDisposeData(lists, dimensionsType);
                } catch (Exception e) {
                    log.error("智能合约数据分析出现异常!!!:{}", e.toString(), e);
                }
            }
            redisValue = JSONObject.toJSONString(resultMap);
            redisClient.set(redisKey,redisValue,indexLoseEfficacyTimesNum);
        }
        return redisValue;
    }

    /**
     * 智能合约出售均价
     * @return
     */
    @Override
    public TOrder usdtSoldPrice(){
        return orderMapper.usdtSoldPrice();
    }

    private Map<String,String[]> complementDisposeData(List<SmartContractHisBillResponse> dbResultLists, String dimensionsType) {
        return disposeData(dbResultLists);
    }

    /**
     * 统计数据 组装
     * @param dbResultLists
     * @return
     */
    private Map<String,String[]> disposeData(List<SmartContractHisBillResponse> dbResultLists) {
        Map<String,String[]> resultMap = new LinkedHashMap<>(); // <日期，[某数1，某数2]>
        for (int i=0;i<dbResultLists.size();i++) {
            SmartContractHisBillResponse result = dbResultLists.get(i);
            String dateStr = result.getDateStr();
            String formatNumber = UdunBigDecimalUtil.defaultFormatBigDecimal(result.getUsdtPrice());
            resultMap.put(dateStr,new String[]{formatNumber});
        }
        return resultMap;
    }


}
