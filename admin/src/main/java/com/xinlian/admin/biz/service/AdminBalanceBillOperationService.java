package com.xinlian.admin.biz.service;

import com.xinlian.common.request.BalanceBillOperationPageReq;
import com.xinlian.common.request.BalanceBillOperationReq;
import com.xinlian.common.response.BalanceBillOperationRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 平账操作记录表 服务类
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
public interface AdminBalanceBillOperationService {

    /**
     * 添加平账操作记录
     * @param req 添加的记录参数
     * @return 添加结果
     */
    ResponseResult addBalanceBillOperation(BalanceBillOperationReq req, HttpServletRequest request);

    /**
     * 分页查询平账操作记录表
     * @param pageReq 分页查询参数
     * @return 分页后列表
     */
    PageResult<List<BalanceBillOperationRes>> findBalanceBillOperationPage(BalanceBillOperationPageReq pageReq);
}
