package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.AdminBillClassify;
import com.xinlian.common.request.BillAuditReq;
import com.xinlian.common.request.BillClassifyShowHideReq;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

/**
 * <p>
 * 账单分类表 服务类
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
public interface AdminBillClassifyService {

    /**
     * 查看所有的账单分类
     * @return 所有账单分类列表
     */
    ResponseResult<List<AdminBillClassify>> findAllBillClassify();
    /**
     * 查询所有隐藏的账单分类
     * @return 隐藏的账单分类列表
     */
    ResponseResult<List<AdminBillClassify>> findNotShowBillClassify();

    /**
     * 修改单个账单分类是否展示
     * @param req 展示属性
     * @return 修改结果
     */
    ResponseResult updateShowBillClassify(BillClassifyShowHideReq req);

    /**
     * 查询当前币种总进账、总出账及总差额，质押人数及金额（包含申请、拒绝、通过三个状态）
     * @param req 请求参数
     * @return 返回实体
     */
    ResponseResult findTotalBill(BillAuditReq req);

    /**
     * 查看账单明细模块列表
     * @param req 请求参数
     * @return 账单明细模块列表
     */
    ResponseResult findBillDetailList(BillAuditReq req);
}
