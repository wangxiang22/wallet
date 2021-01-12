package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.AdminBalanceBillOperationService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.BalanceBillOperationPageReq;
import com.xinlian.common.request.BalanceBillOperationReq;
import com.xinlian.common.response.BalanceBillOperationRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 平账操作记录表 前端控制器
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Slf4j
@Api(value = "平账操作相关接口")
@RestController
@RequestMapping("/adminBalanceBillOperation")
public class AdminBalanceBillOperationController {

    @Autowired
    private AdminBalanceBillOperationService adminBalanceBillOperationService;

    @OpeAnnotation(modelName = OperationModuleEnum.BILL_STATISTICS_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE,opeDesc = "添加平账操作记录")
    @ApiOperation(value = "添加平账操作记录",httpMethod = "POST")
    @PostMapping("/addBalanceBillOperation")
    public ResponseResult addBalanceBillOperation(@RequestBody BalanceBillOperationReq req, HttpServletRequest request) {
        try {
            req.checkParam();
            return adminBalanceBillOperationService.addBalanceBillOperation(req,request);
        }catch (BizException e){
            log.error("添加平账操作记录出现异常:{}",e.getMsg(),e);
            return new ResponseResult(e);
        }catch (Exception e){
            log.error("添加平账操作记录出现异常:{}",e.toString(),e);
            return new ResponseResult(new BizException("添加平账操作记录出现异常！"));
        }
    }

    @ApiOperation(value = "分页查询平账操作记录表",httpMethod = "POST")
    @PostMapping("/findBalanceBillOperationPage")
    public PageResult<List<BalanceBillOperationRes>> findBalanceBillOperationPage(@RequestBody BalanceBillOperationPageReq pageReq) {
        return adminBalanceBillOperationService.findBalanceBillOperationPage(pageReq);
    }
}

