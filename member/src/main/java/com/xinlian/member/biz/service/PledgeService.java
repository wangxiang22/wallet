package com.xinlian.member.biz.service;

import com.xinlian.common.request.PledgeReq;
import com.xinlian.common.response.ResponseResult;

/**
 * 矿池质押 服务类
 * @author lt
 * @since 2020-06-04
 */
public interface PledgeService {
    /**
     *查询矿池质押金额及币种
     * @param userId 用户id
     * @param nodeId 节点id
     * @return 矿池质押金额及币种
     */
    ResponseResult findPledgeAmountCurrency(Long userId,Long nodeId);

    /**
     * 提交质押申请
     * @param pledgeReq 扣除质押金额参数
     * @return 提交结果
     */
    ResponseResult submitPledgeApply(PledgeReq pledgeReq);
}
