package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.UserAuthManagerService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.RefuseReq;
import com.xinlian.common.request.TakeOfferReq;
import com.xinlian.common.request.UserAuthQueryReq;
import com.xinlian.common.request.UserAuthUpdateReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/userAuthManager")
@Api("用户实名管理")
@Slf4j
public class UserAuthManagerController {
    @Autowired
    UserAuthManagerService userAuthManagerService;

    @ApiOperation("查询所有实名信息")
    @PostMapping("/queryAll")
    public ResponseResult queryAll(@RequestBody UserAuthQueryReq userAuthQueryReq){
        try {
            return userAuthManagerService.queryAll(userAuthQueryReq);
        }catch (Exception e){
            log.error("获取所有实名信息出现异常：{}",e.toString(),e);
            return new ResponseResult(new BizException("查询出现异常请稍后尝试!"));
        }
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_REAL_MANAGE,typeName = OperationTypeEnum.REAL_NAME_AUDIT, opeDesc = "通过/批量通过客户实名")
    @ApiOperation("通过/批量通过")
    @PostMapping("/takeOffer")
    public ResponseResult takeOffer(@RequestBody TakeOfferReq takeOfferReq){
        return userAuthManagerService.takeOffer(takeOfferReq.getUids());
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_REAL_MANAGE,typeName = OperationTypeEnum.REAL_NAME_AUDIT, opeDesc = "拒绝/批量拒绝客户实名")
    @ApiOperation("拒绝/批量拒绝")
    @PostMapping("/refuse")
    public ResponseResult refuse(@RequestBody TakeOfferReq takeOfferReq){
        return userAuthManagerService.refuse(takeOfferReq.getUids());
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_REAL_MANAGE,typeName = OperationTypeEnum.REAL_NAME_AUDIT, opeDesc = "拒绝单个人客户实名")
    @ApiOperation("拒绝单个人")
    @PostMapping("/refuseOne")
    public ResponseResult refuseOne(@RequestBody RefuseReq refuseReq){
        return userAuthManagerService.refuseOne(refuseReq);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_REAL_MANAGE,typeName = OperationTypeEnum.REAL_NAME_AUDIT, opeDesc = "客户实名修改")
    @ApiOperation("实名修改")
    @PostMapping("/updateByUid")
    public ResponseResult updateByUid(@RequestBody UserAuthUpdateReq userAuthUpdateReq){
        return userAuthManagerService.updateByUid(userAuthUpdateReq);
    }

    @ApiOperation("重新过滤国内通道已拒绝的实名信息（20200814中午十二点 - 20200819晚上23:59:59的数据）")
    @PostMapping("/batchAuditByBaiduRecognition")
    public void batchAuditByBaiduRecognition(@RequestParam String key) {
        if (!"1166JYYWJYlnzxln".equals(key)){
            return;
        }
        userAuthManagerService.batchAuditByBaiduRecognition();
    }
}

