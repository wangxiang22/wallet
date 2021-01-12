package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.WalletInfoService;
import com.xinlian.common.request.RocketToWalletReq;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "钱包管理")
@RestController
@RequestMapping("/wallet")
public class WalletInfoController {

    @Autowired
    private WalletInfoService walletInfoService;

    @ApiOperation("钱包增加金额接口，主要用来把火箭的钱转过来")
    @PostMapping(value="/updateWalletMoney")
    public ResponseResult updateWalletMoney(@RequestBody RocketToWalletReq req){
        return   walletInfoService.updateWalletMoney(req.getAmount(),req.getCatUid(),req.getCoinName(),req.getRocketPhone(),req.getRocketUid());
    }
}
