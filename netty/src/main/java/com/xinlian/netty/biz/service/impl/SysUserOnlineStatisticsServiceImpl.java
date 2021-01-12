package com.xinlian.netty.biz.service.impl;

import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.*;
import com.xinlian.netty.biz.service.ITOnlineCumulativeDurationService;
import com.xinlian.netty.biz.service.SysUserOnlineStatisticsService;
import com.xinlian.netty.online.OnlineConnectionsManager;
import com.xinlian.netty.online.OnlineWebSocket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

/**
 * @author zhangJun
 * @version V1.0  2020/4/26
 **/

@Service
public class SysUserOnlineStatisticsServiceImpl implements SysUserOnlineStatisticsService {

    @Autowired
    private ITOnlineCumulativeDurationService onlineCumulativeDurationService;

    @Override
    public ResponseResult<OnlineStatisticsVO> onlineStatistics() {
        ConcurrentHashMap<String, Integer> cumulativeDurationMap = OnlineConnectionsManager.getCumulativeDurationMap();
        Collection<Integer> values = cumulativeDurationMap.values();
        List<Integer> more3hList = values.stream().filter(item -> item > (60 * 60 * 3 * 1_000)).collect(Collectors.toList());

        OnlineStatisticsVO onlineStatisticsVO = new OnlineStatisticsVO();

        int liveOnline = 0;
        CopyOnWriteArraySet<OnlineWebSocket> connectionSet = OnlineConnectionsManager.getConnectionSet();
        for (OnlineWebSocket onlineWebSocket : connectionSet) {
            if (StringUtils.isNotEmpty(onlineWebSocket.getUid())) {
                liveOnline++;
            }
        }

        onlineStatisticsVO.setLiveOnline(liveOnline);
        onlineStatisticsVO.setMore3hourOnline(more3hList.size());

//        onlineStatisticsVO.setOnlineDetails(getOnlineDetails(cumulativeDurationMap));

        ResponseResult<OnlineStatisticsVO> result = new ResponseResult<>();
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(onlineStatisticsVO);
        return result;
    }

    @Override
    public ResponseResult<OnlineRankStatisticsVO> onlineRank() {
        OnlineRankStatisticsVO onlineRankStatisticsVO = new OnlineRankStatisticsVO();
        List<OnlineRankVO> todayRank = new LinkedList<>();
        List<OnlineRankVO> totalRank = new LinkedList<>();

        List<UidRelatedUsernameVO> relations = onlineCumulativeDurationService.getUidRelatedUsername();
        Map<String, String> relationMap = relations.stream().collect(toMap(UidRelatedUsernameVO::getUid, UidRelatedUsernameVO::getUsername));
        onlineRankStatisticsVO.setTodayRank(getTodayRank(todayRank, relationMap));
        onlineRankStatisticsVO.setTotalRank(getTotalRank(totalRank, relationMap));

        ResponseResult<OnlineRankStatisticsVO> result = new ResponseResult<>();
        result.setMsg("请求成功");
        result.setCode(GlobalConstant.ResponseCode.SUCCESS);
        result.setResult(onlineRankStatisticsVO);
        return result;
    }

    private List<OnlineRankVO> getTotalRank(List<OnlineRankVO> totalRank, Map<String, String> relationMap) {

        // 累计用户在线排行
        List<OnlineRankVO> list = onlineCumulativeDurationService.sortTotalDurationRank();
        list.forEach(onlineRankVO -> {
                    onlineRankVO.setUsername(Optional.ofNullable(relationMap.get(onlineRankVO.getUid())).orElse("UNKNOWN"));
                }
        );
        totalRank.addAll(list);
        return totalRank;
    }

    private List<OnlineRankVO> getTodayRank(List<OnlineRankVO> todayRank, Map<String, String> relationMap) {

        // 当天用户在线排行
        ConcurrentHashMap<String, Integer> cumulativeDurationMap = OnlineConnectionsManager.getCumulativeDurationMap();
        final LinkedHashMap<String, Integer> todayRankSortMap = cumulativeDurationMap.entrySet().stream()
                .sorted(comparingByValue((o1, o2) -> o2 - o1))
                .limit(50)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        Set<String> todayAllUidSet = cumulativeDurationMap.keySet();
        Set<String> withNameUidSet = relationMap.keySet();
        Set<String> withoutNameUidSet = todayAllUidSet.stream()
                .filter(todayAllUid -> !withNameUidSet.contains(todayAllUid)).collect(Collectors.toSet());

        List<UidRelatedUsernameVO> withoutNameRelations = new ArrayList<>();
        if (withoutNameUidSet.size() != 0) {
            withoutNameRelations = onlineCumulativeDurationService.findUsernameByUidSet(withoutNameUidSet);
        }
        Map<String, String> withoutNameRelationMap = withoutNameRelations.stream()
                .collect(toMap(UidRelatedUsernameVO::getUid, UidRelatedUsernameVO::getUsername));

        todayRankSortMap.forEach((key, value) -> {
            OnlineRankVO onlineRank = new OnlineRankVO();
            onlineRank.setUid(key);
            onlineRank.setUsername(relationMap.get(key) == null ?
                    withoutNameRelationMap.get(key) == null ? "UNKNOWN" : withoutNameRelationMap.get(key)
                    : relationMap.get(key));
            onlineRank.setLastTime(value / 1_000);
            todayRank.add(onlineRank);
        });
        return todayRank;
    }


//    private List<OnlineStatisticsVO.OnlineDetail> getOnlineDetails(ConcurrentHashMap<String, Integer> cumulativeDurationMap) {
//        List<OnlineStatisticsVO.OnlineDetail> onlineDetails = new ArrayList<>();
//
//        List<UserTotalDurationVo> userTotalDurations = onlineCumulativeDurationService.userTotalDurations();
//        Map<String, Integer> userTotalDurationMap = userTotalDurations.stream()
//                .collect(toMap(UserTotalDurationVo::getUid, UserTotalDurationVo::getTotalDuration));
//
//        List<UidRelatedUsernameVO> relations = onlineCumulativeDurationService.getUidRelatedUsername();
//        Map<String, String> relationMap = relations.stream().collect(toMap(UidRelatedUsernameVO::getUid, UidRelatedUsernameVO::getUsername));
//
//
//
//        userTotalDurationMap.forEach((key, value) -> {
//            OnlineStatisticsVO.OnlineDetail onlineDetail = new OnlineStatisticsVO.OnlineDetail();
//            onlineDetail.setUid(key);
//            onlineDetail.setUsername(relationMap.get(key));
//            onlineDetail.setTodayDuration(Optional.ofNullable(cumulativeDurationMap.get(key)).orElse(0));
//            onlineDetail.setTotalDuration(value);
//            onlineDetails.add(onlineDetail);
//        });
//
//        return onlineDetails;
//    }

}
