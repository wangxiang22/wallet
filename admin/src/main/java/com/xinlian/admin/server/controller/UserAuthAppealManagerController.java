package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.UserAuthAppealManagerService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.model.TUserAuthAppeal;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.UserAuthAppealManagerReq;
import com.xinlian.common.request.UserAuthAppealSubmitReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 实名申诉管理接口
 */
@Api(value = "实名申诉管理接口")
@RestController
@RequestMapping(value = "/userAuthAppealManager")
public class UserAuthAppealManagerController {
    @Autowired
    private UserAuthAppealManagerService userAuthAppealManagerService;

    @ApiOperation(value = "根据搜索条件查询实名申诉信息（分页）",httpMethod = "POST")
    @PostMapping("/queryAppealListPage")
    public PageResult<List<TUserAuthAppeal>> queryAppealListPage(@RequestBody UserAuthAppealManagerReq userAuthAppealManagerReq) {
        return userAuthAppealManagerService.queryAppealListPage(userAuthAppealManagerReq);
    }

    @ApiOperation(value = "实名申诉审批操作",httpMethod = "POST")
    @PostMapping("/updateAppealStatus")
    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_REAL_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "实名申诉审批")
    public ResponseResult updateAppealStatus(@RequestBody UserAuthAppealSubmitReq userAuthAppealSubmitReq) {
        return userAuthAppealManagerService.updateAppealStatus(userAuthAppealSubmitReq);
    }
}
