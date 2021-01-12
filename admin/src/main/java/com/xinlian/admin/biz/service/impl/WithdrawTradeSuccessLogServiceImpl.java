package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.service.WithdrawTradeSuccessLogService;
import com.xinlian.biz.dao.TWithdrawTradeSuccessLogMapper;
import com.xinlian.biz.model.TWithdrawTradeSuccessLog;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.WithdrawTradeSuccessLogReq;
import com.xinlian.common.request.WithdrawTradeSuccessTriggerReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.TriggerPopAmountRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WithdrawTradeSuccessLogServiceImpl implements WithdrawTradeSuccessLogService {

    @Autowired
    private TWithdrawTradeSuccessLogMapper withdrawTradeSuccessLogMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Override
    public PageResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessLogListPage(WithdrawTradeSuccessLogReq req) {
        try {
            PageResult<List<TWithdrawTradeSuccessLog>> result = new PageResult<>();
            if (checkObjectFieldIsNull(req)) {
                result.setTotal(withdrawTradeSuccessLogMapper.queryTradeSuccessLogListCount(req));
            }else {
                result.setTotal(withdrawTradeSuccessLogMapper.selectCount(null));
            }
            req.setStartIndex(Integer.parseInt(String.valueOf(req.pickUpOffset())));
            req.setPageSize(req.pickUpPageSize());
            result.setResult(withdrawTradeSuccessLogMapper.queryTradeSuccessLogList(req));
            result.setCurPage(req.pickUpCurPage());
            result.setPageSize(req.pickUpPageSize());
            result.setCode(ErrorInfoEnum.SUCCESS.getCode());
            return result;
        } catch (Exception e) {
            log.error("异常信息：",e);
            throw new BizException("系统繁忙");
        }
    }

    @Override
    public ResponseResult<List<TWithdrawTradeSuccessLog>> queryTradeSuccessTriggerList(WithdrawTradeSuccessTriggerReq req) {
        ResponseResult<List<TWithdrawTradeSuccessLog>> result = new ResponseResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        List<TWithdrawTradeSuccessLog> tradeSuccessTriggerList = withdrawTradeSuccessLogMapper.queryTradeSuccessTriggerList(req);
        if (null == tradeSuccessTriggerList || tradeSuccessTriggerList.size() == 0) {
            result.setResult(null);
            return result;
        }
        TriggerPopAmountRes triggerPopAmountRes = this.findTriggerPopAmountRes();
        if (null == triggerPopAmountRes) {
            result.setCode(ErrorInfoEnum.FAILED.getCode());
            result.setMsg("获取不同币种触发提示弹窗的金额配置出现异常");
            return result;
        }
        List<TWithdrawTradeSuccessLog> resultList = new ArrayList<>();
        for (TWithdrawTradeSuccessLog withdrawTradeSuccessLog : tradeSuccessTriggerList) {
            if (Long.parseLong(CurrencyEnum.USDT.getCurrencyId()+"") == withdrawTradeSuccessLog.getCurrencyId()
                    && StringUtils.isNotBlank(triggerPopAmountRes.getTriggerPopUsdt())
                    && withdrawTradeSuccessLog.getTradeCurrencyNum().abs().compareTo(new BigDecimal(triggerPopAmountRes.getTriggerPopUsdt())) >= 0) {
                resultList.add(withdrawTradeSuccessLog);
            }else if (Long.parseLong(CurrencyEnum.CAT.getCurrencyId()+"") == withdrawTradeSuccessLog.getCurrencyId()
                    && StringUtils.isNotBlank(triggerPopAmountRes.getTriggerPopCat())
                    && withdrawTradeSuccessLog.getTradeCurrencyNum().abs().compareTo(new BigDecimal(triggerPopAmountRes.getTriggerPopCat())) >= 0){
                resultList.add(withdrawTradeSuccessLog);
            }else if (Long.parseLong(CurrencyEnum.CAG.getCurrencyId()+"") == withdrawTradeSuccessLog.getCurrencyId()
                    && StringUtils.isNotBlank(triggerPopAmountRes.getTriggerPopCag())
                    && withdrawTradeSuccessLog.getTradeCurrencyNum().abs().compareTo(new BigDecimal(triggerPopAmountRes.getTriggerPopCag())) >= 0){
                resultList.add(withdrawTradeSuccessLog);
            }
        }
        if (resultList.size() == 0) {
            result.setResult(null);
            return result;
        }
        result.setResult(resultList);
        return result;
    }

    /**
     * 获取不同币种触发提示弹窗的金额配置
     * @return 配置
     */
    private TriggerPopAmountRes findTriggerPopAmountRes() {
        try {
            return adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_TRIGGER_POP_AMOUNT.getBelongsSystemCode(), TriggerPopAmountRes.class);
        } catch (Exception e) {
            log.error("获取不同币种触发提示弹窗的金额配置出现异常：{}",e.toString(),e);
            return null;
        }
    }

    /**
     * 验证对象类字段是否全部为空，全部为空返回false
     * @param object
     * @param excludeFields 排除判断字段
     * @return
     */
    private boolean checkObjectFieldIsNull(Object object,String ... excludeFields) {
        Class objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Object resultFileValue = null;
        boolean flag = false;
        try {
            for (Field field : fields) {
                // 属性名称
                String currentFieldName = field.getName();
                field.setAccessible(true);
                resultFileValue = field.get(object);
                for(String f : excludeFields){
                    if(currentFieldName.toUpperCase().equals(f.toUpperCase())){continue;}
                }
                if(resultFileValue!=null){
                    flag = true;
                    continue;
                }
            }
        }catch(Exception e){
            log.error("checkObjectFieldIsNull反射出现异常:{}", e.toString(), e);
        }
        return flag;
    }
}
