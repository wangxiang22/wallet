package com.xinlian.admin.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.common.response.NewCustomerTrendResponse;
import com.xinlian.common.response.ServerNodeRankResponse;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.common.utils.PrStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * com.xinlian.admin.biz.service
 *
 * @date 2020/2/18 07:32
 */
@Service
@Slf4j
public class UserInfoRegisterService {

    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private RedisClient redisClient;
    private final Long indexLoseEfficacyTimesNum = 2*60*60L;
    /**
     * 获取节点注册排名
     * @param isForceRefresh 是否强制更新
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public Object serverNodeRegisterRanking(boolean isForceRefresh, String startDate, String endDate) {
        String serverNodeRegisterRankingKey = "ADMIN_INDEX_SERVER_NODE_REGISTER_RANKING_KEY_"+startDate+"_"+endDate;
        String redisValue = redisClient.get(serverNodeRegisterRankingKey);
        if(isForceRefresh || redisValue==null){
            List<ServerNodeRankResponse> objectList = userInfoMapper.serverNodeRegisterRank(startDate,endDate);
            //组装 子节点数据归纳到父节点上去
            List<ServerNodeRankResponse> rankLists = packageServerNodeRegisterRankData(objectList);
            redisValue = JSONObject.toJSONString(rankLists);
            redisClient.set(serverNodeRegisterRankingKey ,redisValue ,indexLoseEfficacyTimesNum);
        }
        return redisValue;
    }

    /**
     * 组装节点排名 - 只需要父节点是0的数据
     * @param objectList
     */
    private List<ServerNodeRankResponse> packageServerNodeRegisterRankData(List<ServerNodeRankResponse> objectList) {
        List<ServerNodeRankResponse> parentList = new ArrayList<>();
        objectList.forEach(serverNode ->{
            if(serverNode.getParentId().intValue()==0){
                parentList.add(serverNode);
            }
        });
        // 为顶级节点设置子级节点，getChild是递归调用的
        for (ServerNodeRankResponse serverNodeRankResponse : parentList) {
            serverNodeRankResponse.setChildList(getChild(serverNodeRankResponse.getServerNodeId(), objectList));
        }
        //顶级节点计算
        for(ServerNodeRankResponse parentNode : parentList){
            parentNode.setCollectionAfterRegisterNum(parentNode.getRegisterNum() + getTotalChildNodeRegisterNum(parentNode));
            parentNode.setFormatCollectAfterRegisterNum(PrStringUtils.fmtNumToString(parentNode.getCollectionAfterRegisterNum()));
            parentNode.setChildList(null);
        }
        Collections.sort(parentList,new Comparator<ServerNodeRankResponse>(){
            @Override
            public int compare(ServerNodeRankResponse o1, ServerNodeRankResponse o2) {
                if(o1.getCollectionAfterRegisterNum()<o2.getCollectionAfterRegisterNum()) {
                    return 1;
                }
                if(o1.getCollectionAfterRegisterNum().equals(o2.getCollectionAfterRegisterNum())) {
                    return 0;
                }
                return -1;
            }
        });
        return parentList;
    }



    /**
     * 顶级节点以及下面的节点数据进行汇总
     * @param parentNode
     * @return
     */
    private Long getTotalChildNodeRegisterNum(ServerNodeRankResponse parentNode){
        Long childTotalRegisterNum = 0L;
        if(null!=parentNode.getChildList()){
            for(ServerNodeRankResponse childNode : parentNode.getChildList()){
                childTotalRegisterNum += childNode.getRegisterNum() + this.getTotalChildNodeRegisterNum(childNode);
            }
        }
        return childTotalRegisterNum;
    }

    /**
     * 递归查找子节点
     * @param serverNodeId 当前节点id
     * @param serverNodes 要查找的列表
     * @return
     */
    private List<ServerNodeRankResponse> getChild(Long serverNodeId, List<ServerNodeRankResponse> serverNodes) {
        // 子节点
        List<ServerNodeRankResponse> childList = new ArrayList<>();
        for (ServerNodeRankResponse serverNodeRankResponse : serverNodes) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (!StringUtils.isEmpty(serverNodeRankResponse.getParentIds())) {
                String [] parentIdArray = serverNodeRankResponse.getParentIds().split(",");
                if (parentIdArray.length>=2 && serverNodeId == Long.parseLong(parentIdArray[1])) {
                    childList.add(serverNodeRankResponse);
                }
            }
        }
        // 把子节点的子节点再循环一遍
        for (ServerNodeRankResponse serverNode : childList) {
            // 跟子节点的parentId比较
            if (!StringUtils.isEmpty(serverNode.getParentId())) {
                // 递归
                serverNode.setChildList(getChild(serverNode.getServerNodeId(), serverNodes));
            }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    /**
     * 获取节点激活排名
     * @param isForceRefresh 是否强制更新
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public Object serverNodeActivateRanking(boolean isForceRefresh, String startDate, String endDate) {
        String serverNodeActivateRankingKey = "ADMIN_INDEX_SERVER_NODE_ACTIVATE_RANKING_KEY_"+startDate+"_"+endDate;
        String redisValue = redisClient.get(serverNodeActivateRankingKey);
        if(isForceRefresh || redisValue==null){
            List<ServerNodeRankResponse> objectList = userInfoMapper.serverNodeActivateRank(startDate,endDate);
            //组装 子节点数据归纳到父节点上去
            List<ServerNodeRankResponse> rankLists = packageServerNodeRegisterRankData(objectList);
            redisValue = JSONObject.toJSONString(rankLists);
            redisClient.set(serverNodeActivateRankingKey ,redisValue ,indexLoseEfficacyTimesNum);
        }
        return redisValue;
    }

    /**
     * 新客户走势
     * @param isForceRefresh 是否强制更新
     * @param dimensionsType 统计维度 - 年、月、周、当天
     * @return
     */
    public Object newCustomerTrend(boolean isForceRefresh, String dimensionsType) {
        //从redis取值
        String redisKey = "ADMIN_NEW_CUSTOMER_TREND_"+dimensionsType+"_KEY";
        String redisValue = redisClient.get(redisKey);
        if(null==redisValue || isForceRefresh){
            String lastDayOfWeekTimeStr = CommonUtil.getLastDayOfWeek(new Date());
            String firstDayOfWeekTimeStr = CommonUtil.getFirstDayOfWeek(new Date());
            //调用某个执行sql
            List<NewCustomerTrendResponse> lists =
                    userInfoMapper.statisticsNewCustomerTrend(dimensionsType,firstDayOfWeekTimeStr,lastDayOfWeekTimeStr);
            //补数
            Map<String,String[]> resultMap = complementDisposeData(lists,dimensionsType);
            redisValue = JSONObject.toJSONString(resultMap);
            redisClient.set(redisKey,redisValue,indexLoseEfficacyTimesNum);
        }
        return redisValue;
    }

    private Map<String,String[]> complementDisposeData(List<NewCustomerTrendResponse> dbResultLists, String dimensionsType) {
        if (dimensionsType.trim().equals("YEAR")) {
            return disposeData(dbResultLists,12);
        } else if (dimensionsType.trim().equals("MONTH")) {
            return disposeData(dbResultLists,CommonUtil.getCurrentMonthLastDay());
        } else if (dimensionsType.trim().equals("WEEK")) {
            return disposeData(dbResultLists,7);
        } else{
            return disposeData(dbResultLists,24);
        }
    }

    /**
     * 统计数据 补全
     * @param dbResultLists
     * @param cycleNum
     * @return
     */
    private Map<String,String[]> disposeData(List<NewCustomerTrendResponse> dbResultLists,int cycleNum) {
        Map<String,String[]> resultMap = new LinkedHashMap<>(); // <日期，[某数1，某数2]>
        int forI = 0 ;
        for (int i=1;i<=cycleNum;i++) {
            if(null==dbResultLists||0==dbResultLists.size()){
                resultMap.put(i+"",new String[]{"0"});
                continue;
            }
            if(forI>=dbResultLists.size()){forI = dbResultLists.size()-1;}
            NewCustomerTrendResponse result = dbResultLists.get(forI);
            int month = result.getDateStr();
            if(month>i){
                for(int j=i;j<month;j++){
                    resultMap.put(j+"",new String[]{"0"});
                }
                resultMap.put(month+"",new String[]{result.getRegisterNum()+""});
                forI++;
                i=month;
            }else if(month==i){
                resultMap.put(month+"",new String[]{result.getRegisterNum()+""});
                forI++;
            }else{
                resultMap.put(i+"",new String[]{"0"});
            }
        }
        return resultMap;
    }


}
