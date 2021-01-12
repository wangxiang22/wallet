package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.CurrencyManagerService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.model.TCurrencyManage;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("币种管理")
@RestController
@RequestMapping("/trade")
public class CurrencyManagerController {
    @Autowired
    private CurrencyManagerService currencyManagerService;

    @PostMapping("/v1/currency/lists")
    @ApiOperation("查询所有币种")
    public ResponseResult queryAllCurrency(){
        return currencyManagerService.queryAllCurrency();
    }


    @OpeAnnotation(modelName = OperationModuleEnum.CURRENCY_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改币种接口")
    @PostMapping("/v1/currency/update")
    @ApiOperation("查询所有币种")
    public ResponseResult updateCurrencyInfo(TCurrencyManage tCurrencyManage){
        return currencyManagerService.udpateCurrencyInfo(tCurrencyManage);
    }
}
