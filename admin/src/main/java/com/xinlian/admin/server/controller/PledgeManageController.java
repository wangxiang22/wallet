package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.PledgeManageService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.common.enums.OperationLogLevelEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.PledgeAuditReq;
import com.xinlian.common.request.PledgeManagePageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.PledgeManagePageRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 矿池质押审核相关接口
 */
@Slf4j
@Api(value = "矿池质押审核相关接口")
@RestController
@RequestMapping("/pledgeManage")
public class PledgeManageController {
    @Autowired
    private PledgeManageService pledgeManageService;

    @ApiOperation(value = "分页查询算能质押申请信息",httpMethod = "POST")
    @PostMapping("/findPledgePage")
    public PageResult<List<PledgeManagePageRes>> findPledgePage(@RequestBody PledgeManagePageReq pledgeManagePageReq) {
        return pledgeManageService.findPledgePage(pledgeManagePageReq);
    }

    @ApiOperation(value = "审核算能质押",httpMethod = "POST")
    @PostMapping("/auditPledge")
    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "质押审核")
    public ResponseResult auditPledge(@RequestBody PledgeAuditReq req) {
        try {
            return pledgeManageService.auditPledge(req);
        } catch (BizException e) {
            log.error("审核算能质押接口异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("审核算能质押接口异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }


    @ApiOperation(value = "审核算能质押一键审核通过-锁表中，不要随意操作",httpMethod = "POST")
    @PostMapping("/auditPledge/batch")
    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "批量质押审核通过")
    public ResponseResult allAuditPledge(@RequestParam List<Long> uid) {
        try {
            pledgeManageService.batchAuditPledgeToPass(uid);
            return ResponseResult.ok();
        } catch (BizException e) {
            log.error("审核算能质押全部通过接口业务异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("审核算能质押全部通过接口系统异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }

    @ApiOperation(value = "审核算能质押一键审核通过-锁表中，不要随意操作",httpMethod = "POST")
    @PostMapping("/auditPledge/all")
    @OpeAnnotation(typeName = OperationTypeEnum.OTHER_OPERATE, logLevel = OperationLogLevelEnum.INFO,opeDesc = "质押全部审核通过")
    public ResponseResult allAuditPledgeByAuth(@RequestParam String auth) {
        try {
            if(!"auth!2000".equals(auth)){return ResponseResult.error();}
            pledgeManageService.batchAuditPledgeToPass(null);
            return ResponseResult.ok();
        } catch (BizException e) {
            log.error("审核算能质押全部通过接口业务异常:{}", e.toString(), e);
            return new ResponseResult(e);
        } catch (Exception e) {
            log.error("审核算能质押全部通过接口系统异常:{}", e.toString(), e);
            return new ResponseResult(new BizException(ErrorInfoEnum.FAILED));
        }
    }
}
