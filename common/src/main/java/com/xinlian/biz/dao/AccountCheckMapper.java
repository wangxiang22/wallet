package com.xinlian.biz.dao;

import com.xinlian.biz.dao.mapper.base.BasicMapper;
import com.xinlian.biz.model.AccountCheckModel;
import com.xinlian.biz.model.StatisticsTradeOrderModel;
import com.xinlian.common.response.TopNodeTradeDataResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 财务核查表 Mapper 接口
 * </p>
 *
 * @author 无名氏
 * @since 2020-04-15
 */
@Component
public interface AccountCheckMapper extends BasicMapper<AccountCheckModel,Long> {

    /**
     * 获取币种对应总量
     * @return
     */
    List<AccountCheckModel> getTotalByCurrency();

    /**
     * 获取待汇总分节点数据
     * @return
     */
    List<TopNodeTradeDataResponse> queryStaySummary(@Param("clearDay")String reconcileDate);

    /**
     * 统计 - 日期，节点 维度下的 站外提现，充值等属性
     * @param accountCheckModel
     * @return
     */
    List<StatisticsTradeOrderModel> staticTradeOrderTradeNum(AccountCheckModel accountCheckModel);

    /**
     *
     * @param clearDay
     */
    int staticWalletInfo(@Param("clearDay") String clearDay);

    int createStaticTask(@Param("staticTaskName")String staticTaskName,@Param("staticDate")String staticDate);

    int batchUpdate(List<StatisticsTradeOrderModel> list);
}
