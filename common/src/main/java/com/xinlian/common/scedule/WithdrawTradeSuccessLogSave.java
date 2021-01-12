package com.xinlian.common.scedule;

import com.xinlian.biz.dao.TWithdrawTradeSuccessLogMapper;
import com.xinlian.biz.model.TWithdrawTradeSuccessLog;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.response.TriggerRecordAmountRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Component
public class WithdrawTradeSuccessLogSave {

    @Autowired
    private TWithdrawTradeSuccessLogMapper withdrawTradeSuccessLogMapper;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    @Async
    public void addWithdrawTradeSuccessLog(Long uid, Long currencyId, String currencyCode, Long counterPartyUid, String tradeAddress,
                                           BigDecimal tradeCurrencyNum, String des, String txId, Date createTime, String uniqueCode) {
        TriggerRecordAmountRes triggerRecordAmountRes = this.findTriggerRecordAmountRes();
        if (null == triggerRecordAmountRes) {
            return;
        }
        //不同币种判断金额是否符合需要记录的金额
        //最外面设置一个变量，接收配置项中的对应币种的记录金额，然后与传值过来的金额绝对值对比大小，符合条件的再继续后面写入表的操作
        BigDecimal triggerRecordAmount = BigDecimal.ZERO;
        if (CurrencyEnum.USDT.getCurrencyId() == currencyId && StringUtils.isNotBlank(triggerRecordAmountRes.getTriggerRecordUsdt())) {
            triggerRecordAmount = new BigDecimal(triggerRecordAmountRes.getTriggerRecordUsdt());
        }else if (CurrencyEnum.CAT.getCurrencyId() == currencyId && StringUtils.isNotBlank(triggerRecordAmountRes.getTriggerRecordCat())){
            triggerRecordAmount = new BigDecimal(triggerRecordAmountRes.getTriggerRecordCat());
        }else if (CurrencyEnum.CAG.getCurrencyId() == currencyId && StringUtils.isNotBlank(triggerRecordAmountRes.getTriggerRecordCag())){
            triggerRecordAmount = new BigDecimal(triggerRecordAmountRes.getTriggerRecordCag());
        }
        if (tradeCurrencyNum.abs().compareTo(triggerRecordAmount) < 0) {
            return;
        }
        TWithdrawTradeSuccessLog withdrawTradeSuccessLog = new TWithdrawTradeSuccessLog();
        withdrawTradeSuccessLog.setUid(uid);
        withdrawTradeSuccessLog.setCurrencyId(currencyId);
        withdrawTradeSuccessLog.setCurrencyCode(currencyCode);
        if (null != counterPartyUid) {
            withdrawTradeSuccessLog.setCounterPartyUid(counterPartyUid);
        }
        if (StringUtils.isNotBlank(tradeAddress)) {
            withdrawTradeSuccessLog.setTradeAddress(tradeAddress);
        }
        withdrawTradeSuccessLog.setTradeCurrencyNum(tradeCurrencyNum);
        withdrawTradeSuccessLog.setDes(des);
        if (StringUtils.isNotBlank(txId)) {
            withdrawTradeSuccessLog.setTxId(txId);
        }
        withdrawTradeSuccessLog.setCreateTime(createTime);
        withdrawTradeSuccessLog.setUniqueCode(uniqueCode);
        withdrawTradeSuccessLogMapper.insert(withdrawTradeSuccessLog);
    }

    /**
     * 获取触发不同币种写入资金变动流水记录表的金额配置
     * @return 配置
     */
    private TriggerRecordAmountRes findTriggerRecordAmountRes() {
        try {
            return adminOptionsUtil.fieldEntityObject(AdminOptionsBelongsSystemCodeEnum.APP_TRIGGER_RECORD_AMOUNT.getBelongsSystemCode(), TriggerRecordAmountRes.class);
        } catch (Exception e) {
            log.error("查找触发不同币种写入资金变动流水记录表的金额出现异常：{}",e.toString(),e);
            return null;
        }
    }
}
