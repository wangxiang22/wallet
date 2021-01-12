package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.WithdrawCustomerModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 容许提现usdt客户表 Mapper 接口
 * </p>
 *
 * @since 2020-05-25
 */
@Component
public interface WithdrawCustomerMapper extends BaseMapper<WithdrawCustomerModel> {

    WithdrawCustomerModel getByCriteria(WithdrawCustomerModel withdrawCustomerModel);

    int updateModel(WithdrawCustomerModel withdrawCustomerModel);

    /**
     * 获取最新一条记录
     * @param uid 用户id
     * @return entity
     */
    WithdrawCustomerModel getWithdrawStatus(@Param(value = "uid") Long uid);

    /**
     * 查询状态可用集合
     * @return
     */
    List<WithdrawCustomerModel> query();
}
