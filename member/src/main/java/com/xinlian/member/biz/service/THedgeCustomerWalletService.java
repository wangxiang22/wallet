package com.xinlian.member.biz.service;

import com.xinlian.common.request.HedgeCustomerReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 冻结客户资产记录表 服务类
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
public interface THedgeCustomerWalletService {
    /**
     * 添加算力地球用户冻结保证金资产记录
     * @param hedgeCustomerReq
     * @return
     */
    ResponseResult addMiningHedgeCustomer(HedgeCustomerReq hedgeCustomerReq);
}