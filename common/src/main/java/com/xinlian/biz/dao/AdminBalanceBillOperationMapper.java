package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AdminBalanceBillOperation;
import com.xinlian.common.request.BalanceBillOperationPageReq;
import com.xinlian.common.response.BalanceBillOperationRes;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 平账操作记录表 Mapper 接口
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Component
public interface AdminBalanceBillOperationMapper extends BaseMapper<AdminBalanceBillOperation> {

    /**
     * 分页查询平账操作记录表
     * @param pageReq 分页查询参数
     * @return 分页后列表
     */
    List<BalanceBillOperationRes> findBalanceBillOperationPage(BalanceBillOperationPageReq pageReq);

    /**
     * 平账进账金额
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平账进账金额
     */
    BigDecimal findBalanceBillTakeInAmount(Long currencyId, String startTime, String endTime);

    /**
     * 平账出账金额
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平账出账金额
     */
    BigDecimal findBalanceBillExpenditureAmount(Long currencyId, String startTime, String endTime);
}
