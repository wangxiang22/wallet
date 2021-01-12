package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.AdminBillClassifyService;
import com.xinlian.admin.biz.service.TemplateExportExcelService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.model.AdminBillClassify;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.BillAuditReq;
import com.xinlian.common.request.BillClassifyShowHideReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 账单分类表 前端控制器
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Slf4j
@Api(value = "账单分类相关接口")
@RestController
@RequestMapping("/adminBillClassify")
public class AdminBillClassifyController {

    @Autowired
    private AdminBillClassifyService adminBillClassifyService;
    @Autowired
    private TemplateExportExcelService templateExportExcelService;

    @ApiOperation(value = "查看所有的账单分类",httpMethod = "POST")
    @PostMapping("/findAllBillClassify")
    public ResponseResult<List<AdminBillClassify>> findAllBillClassify() {
        return adminBillClassifyService.findAllBillClassify();
    }

    @ApiOperation(value = "查询所有隐藏的账单分类",httpMethod = "POST")
    @PostMapping("/findNotShowBillClassify")
    public ResponseResult<List<AdminBillClassify>> findNotShowBillClassify() {
        return adminBillClassifyService.findNotShowBillClassify();
    }

    @OpeAnnotation(modelName = OperationModuleEnum.BILL_STATISTICS_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE,opeDesc = "修改单个账单分类是否展示")
    @ApiOperation(value = "修改单个账单分类是否展示",httpMethod = "POST")
    @PostMapping("/updateShowBillClassify")
    public ResponseResult updateShowBillClassify(@RequestBody BillClassifyShowHideReq req) {
        return adminBillClassifyService.updateShowBillClassify(req);
    }

    @ApiOperation(value = "查询当前币种总进账、总出账及总差额，质押人数及金额（包含申请、拒绝、通过三个状态）",httpMethod = "POST")
    @PostMapping("/findTotalBill")
    public ResponseResult findTotalBill(@RequestBody BillAuditReq req) {
        try {
            return adminBillClassifyService.findTotalBill(req);
        }catch (BizException e){
            log.error("查询当前币种总进账、总出账及总差额出现异常:{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error("查询当前币种总进账、总出账及总差额出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("查询当前币种总进账、总出账及总差额出现异常！"));
        }
    }

    @ApiOperation(value = "查看账单明细模块列表",httpMethod = "POST")
    @PostMapping("/findBillDetailList")
    public ResponseResult findBillDetailList(@RequestBody BillAuditReq req) {
        try {
            return adminBillClassifyService.findBillDetailList(req);
        }catch (BizException e){
            log.error("查看账单明细模块列表出现异常:{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error("查看账单明细模块列表出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("查看账单明细模块列表出现异常！"));
        }
    }

    @ApiOperation(value = "导出每日资产统计数据",httpMethod = "POST")
    @GetMapping("/exportEveryDayBillDetail")
    public void exportEveryDayBillDetail(HttpServletResponse response, @RequestParam String reqTime) {
        try {
            templateExportExcelService.exportEveryDayBillDetail(response,reqTime);
        } catch (IOException e) {
            log.error("下载文件失败");
            throw new BizException("下载文件失败");
        }
    }

}

