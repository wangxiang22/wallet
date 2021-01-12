package com.xinlian.member.biz.scheduling;

import com.xinlian.biz.dao.SpotLogMapper;
import com.xinlian.biz.model.SpotLog;
import com.xinlian.common.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class SpotLogSave {
    @Autowired
    private SpotLogMapper spotLogMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    /**
     *
     * @param uid
     * @param orderId
     */

    @Async
    public void addLog(Long uid, String orderId,String involveTradeOrderIds,String tradeType){
        String ipAddress = CommonUtil.getIPAddress(httpServletRequest);
        String device = CommonUtil.getDevice(httpServletRequest);
        SpotLog spotLog = new SpotLog();
        spotLog.setDevice(device);
        spotLog.setIp(ipAddress);
        spotLog.setUid(uid.toString());
        spotLog.setOrderId(orderId);
        spotLog.setCreateTime(new Date());
        spotLog.setTradeType(tradeType);
        spotLog.setInvolveTradeOrderIds(involveTradeOrderIds);
        spotLogMapper.insert(spotLog);
    }

}
