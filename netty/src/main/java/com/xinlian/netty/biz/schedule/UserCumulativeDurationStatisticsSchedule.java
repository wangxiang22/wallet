package com.xinlian.netty.biz.schedule;

import com.xinlian.biz.model.TOnlineCumulativeDuration;
import com.xinlian.common.scedule.AbstractSchedule;
import com.xinlian.netty.biz.service.ITOnlineCumulativeDurationService;
import com.xinlian.netty.online.OnlineConnectionsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangJun
 * @version V1.0  2020/4/26
 * <p>
 * 统计用户当天累计在线时长,存入DB >>
 * </p>
 **/
@Slf4j
@Component
public class UserCumulativeDurationStatisticsSchedule extends AbstractSchedule {

    @Autowired
    private ITOnlineCumulativeDurationService onlineCumulativeDurationService;

    @Override
    protected String getCron() {
        /* 每天0点执行 */
        return "0 0 0 */1 * ?";
//        return "0 0/1 * * * ?";
    }

    @Override
    public void doSchedule() {
        ConcurrentHashMap<String, Integer> cumulativeDurationMap = OnlineConnectionsManager.getCumulativeDurationMap();
        List<TOnlineCumulativeDuration> list = new LinkedList<>();
        LocalDate date = LocalDate.now();
        date = date.minusDays(1);
        for (String key : cumulativeDurationMap.keySet()) {
            TOnlineCumulativeDuration entity = new TOnlineCumulativeDuration();
            entity.setUid(key);
            entity.setLastTime(cumulativeDurationMap.get(key) / 1_000);
            entity.setDate(date);
            list.add(entity);
        }
        OnlineConnectionsManager.clearCumulativeDurationMap();
        if (list.size() != 0) {
            onlineCumulativeDurationService.insertBatch(list);
        }
    }
}
