package com.xinlian.admin.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.service.AdminBalanceBillOperationService;
import com.xinlian.biz.dao.AdminBalanceBillOperationMapper;
import com.xinlian.biz.model.AdminBalanceBillOperation;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.BalanceBillOperationPageReq;
import com.xinlian.common.request.BalanceBillOperationReq;
import com.xinlian.common.response.BalanceBillOperationRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 平账操作记录表 服务实现类
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Service
public class AdminBalanceBillOperationServiceImpl implements AdminBalanceBillOperationService {

    @Autowired
    private AdminBalanceBillOperationMapper adminBalanceBillOperationMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ResponseResult addBalanceBillOperation(BalanceBillOperationReq req, HttpServletRequest request) {
        int currencyId = CurrencyEnum.getCurrencyIdByCurrencyCode(req.getCurrencyName());
        if(currencyId==0) {
            throw new BizException("找不到对应currencyName:【"+req.getCurrencyName()+"】");
        }
        AdminBalanceBillOperation adminBalanceBillOperation = new AdminBalanceBillOperation();
        adminBalanceBillOperation.setCurrencyId(Long.parseLong(currencyId+""));
        adminBalanceBillOperation.setCurrencyName(req.getCurrencyName());
        adminBalanceBillOperation.setBillName(req.getBillName());
        if (req.getHedgeAmount() == null){
            req.setHedgeAmount(BigDecimal.ZERO);
        }
        adminBalanceBillOperation.setHedgeAmount(req.getHedgeAmount().setScale(8, BigDecimal.ROUND_DOWN));
        adminBalanceBillOperation.setHedgeTime(req.getHedgeTime());
        adminBalanceBillOperation.setRemarks(req.getRemarks());
        adminBalanceBillOperation.setOperator(jwtUtil.getUserName(request));
        adminBalanceBillOperation.setOperationTime(new Date());
        Integer insertResult = adminBalanceBillOperationMapper.insert(adminBalanceBillOperation);
        if (0 == insertResult) {
            return ResponseResult.error("添加平账操作记录失败");
        }
        return ResponseResult.ok();
    }

    @Override
    public PageResult<List<BalanceBillOperationRes>> findBalanceBillOperationPage(BalanceBillOperationPageReq pageReq) {
        PageResult<List<BalanceBillOperationRes>> result = new PageResult<>();
        result.setCode(ErrorInfoEnum.SUCCESS.getCode());
        PageHelper.startPage(pageReq.getPageNum(),pageReq.getPageSize());
        List<BalanceBillOperationRes> balanceBillOperationResList = adminBalanceBillOperationMapper.findBalanceBillOperationPage(pageReq);
        if (null != balanceBillOperationResList && balanceBillOperationResList.size() > 0) {
            PageInfo<BalanceBillOperationRes> pageInfo = new PageInfo<>(balanceBillOperationResList);
            result.setCurPage(pageInfo.getPageNum());
            result.setPageSize(pageInfo.getPageSize());
            result.setTotal(pageInfo.getTotal());
            result.setResult(balanceBillOperationResList);
        }
        return result;
    }
}
