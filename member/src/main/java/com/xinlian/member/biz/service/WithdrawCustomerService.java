package com.xinlian.member.biz.service;

import com.xinlian.biz.model.WithdrawCustomerModel;
import com.xinlian.common.request.WithdrawCurrencyRequest;

/**
 * 容许提现usdt客户表 服务类
 */
public interface WithdrawCustomerService {

    WithdrawCustomerModel getByCriteria(WithdrawCustomerModel withdrawCustomerModel);

    /**
     * 根据请求参数 获取当前请求所在uid是否 在特殊容许列表中
     * @param withdrawCurrencyRequest
     * @return
     */
    WithdrawCustomerModel judgeWithdrawCustomer(WithdrawCurrencyRequest withdrawCurrencyRequest);


    int updateModel(WithdrawCustomerModel model);


    void initWithdrawCustomerToCache(String redisKey);

    boolean checkWithdrawCustomerUid(Long uid);
}
