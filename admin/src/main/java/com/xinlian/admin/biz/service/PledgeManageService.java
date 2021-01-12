package com.xinlian.admin.biz.service;

import com.xinlian.common.request.PledgeAuditReq;
import com.xinlian.common.request.PledgeManagePageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.PledgeManagePageRes;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

/**
 * 矿池质押审核 服务类
 * @author lt
 * @since 2020-06-11
 */
public interface PledgeManageService {
    /**
     * 分页查询算能质押申请信息
     * @param pledgeManagePageReq
     * @return
     */
    PageResult<List<PledgeManagePageRes>> findPledgePage(PledgeManagePageReq pledgeManagePageReq);

    /**
     * 审核算能质押
     * @param req
     * @return
     */
    ResponseResult auditPledge(PledgeAuditReq req);

    /**
     * 批量审核算能质押-通过
     * @param uid
     * @return
     */
    void batchAuditPledgeToPass(List<Long> uid);
}
