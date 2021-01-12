package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.AccountCheckMapper;
import com.xinlian.biz.model.AccountCheckModel;
import com.xinlian.biz.model.StatisticsTradeOrderModel;
import com.xinlian.common.enums.StatisticsTaskNameEnum;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.common.response.TopNodeTradeDataResponse;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AccountCheckService extends PageBaseService<AccountCheckModel> {

    @Autowired
    private AccountCheckMapper accountCheckMapper;

    @Override
    public List<AccountCheckModel> query(AccountCheckModel model) throws Exception {
        return accountCheckMapper.query(model);
    }

    /**
     * 统计某天钱包各节点币种总额
     * @param clearDay
     */
    public void staticWalletInfo(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_WALLET_INFO.getDesc(),clearDay)){return;}
        accountCheckMapper.staticWalletInfo(clearDay);
    }

    /**
     * 统计订单-链上充值
     * @param clearDay
     * @return
     */
    @Transactional
    public void staticOffSiteRechargeNum(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_OFF_SITE_RECHARGE_NUM.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        accountCheckModel.setTradeType(WalletTradeTypeEnum.TOP_UP.getTradeType());
        accountCheckModel.setDes(WalletTradeTypeEnum.TOP_UP.getTradeDesc());
        List<StatisticsTradeOrderModel> resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    /**
     * 站外提现
     * @param clearDay
     * @return
     */
    @Transactional
    public void staticOffSiteWithdraw(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_OFF_SITE_WITHDRAW.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        accountCheckModel.setTradeType(WalletTradeTypeEnum.MENTION_MONEY.getTradeType());
        accountCheckModel.setDes(WalletTradeTypeEnum.MENTION_MONEY.getTradeDesc());
        List resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    /**
     * 来自火箭数值
     * @param clearDay
     * @return
     */
    @Transactional
    public void staticFromRocketNum(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_FROM_ROCKET_NUM.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        accountCheckModel.setTradeType(WalletTradeTypeEnum.FROM_ROCKET.getTradeType());
        accountCheckModel.setDes(WalletTradeTypeEnum.FROM_ROCKET.getTradeDesc());
        List resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    /**
     * 转到火箭数值
     * @param clearDay
     */
    @Transactional
    public void staticToRocketNum(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_TO_ROCKET_NUM.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        accountCheckModel.setTradeType(WalletTradeTypeEnum.TO_ROCKET.getTradeType());
        accountCheckModel.setDes(WalletTradeTypeEnum.TO_ROCKET.getTradeDesc());
        List resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    private boolean judgeCreateStaticTaskName(String staticTaskName,String clearDay){
        try {
            if(null==clearDay){
                clearDay = DateFormatUtil.get(7,new Date());
                staticTaskName = staticTaskName + "至" + clearDay;
            }
            int result = accountCheckMapper.createStaticTask(staticTaskName,clearDay);
            return result<1;
        }catch (Exception e){
            log.error("插入统计任务name:{}-{}出现异常!",staticTaskName,clearDay);
            return true;
        }
    }

    /**
     * 其他入账
     * @param clearDay
     * @return
     */
    @Transactional
    public void staticOtherRecorded(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_OTHER_RECORDED.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        //1 是入账
        accountCheckModel.setTradeType(1);
        String [] array = new String[2];
        array[0] = WalletTradeTypeEnum.TOP_UP.getTradeDesc();
        array[1] = WalletTradeTypeEnum.FROM_ROCKET.getTradeDesc();
        accountCheckModel.setOtherStatistics(array);
        List resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    /**
     * 其他出账
     * @param clearDay
     * @return
     */
    @Transactional
    public void staticOtherChargeOff(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.STATIC_OTHER_CHARGE_OFF.getDesc(),clearDay)){return;}
        AccountCheckModel accountCheckModel = new AccountCheckModel();
        accountCheckModel.setStaticsDate(clearDay);
        //2 是出账
        accountCheckModel.setTradeType(2);
        String [] array = new String[4];
        array[0] = WalletTradeTypeEnum.MENTION_MONEY.getTradeDesc();
        array[1] = WalletTradeTypeEnum.TO_ROCKET.getTradeDesc();
        array[2] = WalletTradeTypeEnum.INTERNAL_TRADE_TO.getTradeDesc();
        array[3] = WalletTradeTypeEnum.INTERNAL_TRADE_ADD.getTradeDesc();
        accountCheckModel.setOtherStatistics(array);
        List resultList = accountCheckMapper.staticTradeOrderTradeNum(accountCheckModel);
        if(resultList.size()>0){
            //update 财务核查表 .更新条件是 serverNode,币种id
            accountCheckMapper.batchUpdate(resultList);
        }
    }

    /**
     * 把数据汇总到顶级节点下
     * @param clearDay
     */
    public void summaryTopNodeData(String clearDay) {
        if(judgeCreateStaticTaskName(StatisticsTaskNameEnum.SUMMARY_TOP_NODE_DATA.getDesc(),clearDay)){;}
        List<TopNodeTradeDataResponse> staySummaryList = accountCheckMapper.queryStaySummary(clearDay);
        //组装 子节点数据归纳到父节点上去
        List list = packageServerNodeAccountCheckData(staySummaryList);
        System.err.println(list);
    }

    private List packageServerNodeAccountCheckData(List<TopNodeTradeDataResponse> objectList) {
        List<TopNodeTradeDataResponse> parentList = new ArrayList<TopNodeTradeDataResponse>();
        objectList.forEach(getModel ->{
            //顶级节点 或者是 之前已经汇总的全部节点
            if(getModel.getParentId().intValue()==0 ||  null == getModel.getParentId()){
                parentList.add(getModel);
            }
        });
        // 为顶级节点设置子级节点，getChild是递归调用的
        for (TopNodeTradeDataResponse parentModel : parentList) {
            parentModel.setChildList(getChild(parentModel.getServerNodeId(), objectList));
        }
        //顶级节点计算
        for(TopNodeTradeDataResponse parentNode : parentList){
            parentNode.setCurrentTotalCurrencyNum(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"currentTotalCurrencyNum")));
            parentNode.setOffSiteRechargeNum(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"offSiteRechargeNum")));
            parentNode.setOffSiteWithdraw(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"offSiteWithdraw")));
            parentNode.setFromRocketNum(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"fromRocketNum")));
            parentNode.setToRocketNum(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"toRocketNum")));
            parentNode.setOtherRecorded(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"otherRecorded")));
            parentNode.setOtherChargeOff(UdunBigDecimalUtil.addNum(parentNode.getCurrentTotalCurrencyNum(), getTotalChildNodeSummaryNum(parentNode,"otherChargeOff")));
            parentNode.setChildList(null);
        }
        return null;
    }

    /**
     * 顶级节点以及下面的节点数据进行汇总
     * @param childNodeObj
     * @return
     */
    private BigDecimal getTotalChildNodeSummaryNum(TopNodeTradeDataResponse childNodeObj,String targetFileName){
        BigDecimal childTotalNum = new BigDecimal("0.00");
        if(null!=childNodeObj.getChildList()){
            for(TopNodeTradeDataResponse childNode : childNodeObj.getChildList()){
                BigDecimal nodeValue = getFieldValueByObject(childNode,targetFileName,BigDecimal.class);
                childTotalNum = UdunBigDecimalUtil.addNum(nodeValue,this.getTotalChildNodeSummaryNum(childNode,targetFileName));
            }
        }
        return childTotalNum;
    }

    private static <T> T getFieldValueByObject(Object object, String targetFieldName,Class<T> clazz) {
        Class objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Object resultFileValue = null;
        try {
            for (Field field : fields) {
                // 属性名称
                String currentFieldName = field.getName();
                if (currentFieldName.equals(targetFieldName)) {
                    field.setAccessible(true);
                    resultFileValue = field.get(object);
                }
            }
        }catch(Exception e){
            log.error("getFieldValueByObject反射出现异常:", e.toString(), e);
        }
        if(null==resultFileValue) {return null;}
        return (T)resultFileValue;
    }


    /**
     * 递归查找子节点
     * @param serverNodeId 当前节点id
     * @param serverNodes 要查找的列表
     * @return
     */
    private List<TopNodeTradeDataResponse> getChild(Long serverNodeId, List<TopNodeTradeDataResponse> serverNodes) {
        // 子节点
        List<TopNodeTradeDataResponse> childList = new ArrayList<>();
        for (TopNodeTradeDataResponse serverNodeRankResponse : serverNodes) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (!StringUtils.isEmpty(serverNodeRankResponse.getParentId())) {
                if (serverNodeRankResponse.getParentId().equals(serverNodeId)) {
                    childList.add(serverNodeRankResponse);
                }
            }
        }
        // 把子节点的子节点再循环一遍
        for (TopNodeTradeDataResponse serverNode : childList) {
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

}
