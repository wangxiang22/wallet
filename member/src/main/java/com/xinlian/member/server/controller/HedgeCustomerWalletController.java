package com.xinlian.member.server.controller;


import com.xinlian.common.request.HedgeCustomerReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.jwt.annotate.PassToken;
import com.xinlian.member.biz.service.THedgeCustomerWalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 冻结客户资产记录表 前端控制器
 * </p>
 *
 * @author lt
 * @since 2020-05-29
 */
@Api(value = "冻结客户资产接口")
@RestController
@RequestMapping("/{versionPath}/hedgeCustomerWallet")
public class HedgeCustomerWalletController {
    @Autowired
    private THedgeCustomerWalletService hedgeCustomerWalletService;

    @ApiOperation(value = "添加算力地球用户冻结保证金资产记录",httpMethod = "POST")
    @PostMapping("/addMiningHedgeCustomer")
    @PassToken
    public ResponseResult addMiningHedgeCustomer(@RequestBody HedgeCustomerReq hedgeCustomerReq) {
        return hedgeCustomerWalletService.addMiningHedgeCustomer(hedgeCustomerReq);
    }
}