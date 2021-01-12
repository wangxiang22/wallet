package com.xinlian.member.server.controller;


import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.IRegisterLoginService;
import com.xinlian.member.biz.service.TUserExchangeWalletService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjf
 * @since 2019-12-28
 */
@RestController
@RequestMapping("/{versionPath}/tUserExchangeWallet")
@Api("交易所钱包")
public class TUserExchangeWalletController {
    @Autowired
    private TUserExchangeWalletService tUserExchangeWalletService;
    @Autowired
    private IRegisterLoginService registerLoginService;
    @Autowired
    private JwtUtil jwtUtil;

//    @ApiOperation("查询绑定的状态")
//    @PostMapping("queryBindState")
//    public ResponseResult queryBindState(HttpServletRequest httpServletRequest) {
//        Long uid = jwtUtil.getUserId(httpServletRequest);
//        return tUserExchangeWalletService.queryBindState(uid);
//    }

//    @ApiOperation("绑定交易所")
//    @PostMapping("BindExchange")
//    public ResponseResult BindExchange(@RequestBody TUserExchangeWallet tUserExchangeWallet, HttpServletRequest httpServletRequest) {
//        tUserExchangeWallet.setCatUid(jwtUtil.getUserId(httpServletRequest));
//        return tUserExchangeWalletService.BindExchange(tUserExchangeWallet);
//    }

//    @ApiOperation("钱包解绑交易所")
//    @PostMapping("delBind")
//    public ResponseResult delBind(@RequestBody DelBindReq delBindReq, HttpServletRequest httpServletRequest) {
//        delBindReq.setUid(jwtUtil.getUserId(httpServletRequest));
//        return tUserExchangeWalletService.delBindReq(delBindReq);
//    }

//    @ApiOperation("钱包解绑交易所验证码")
//    @PostMapping("getDelCode")
//    public ResponseResult getDelCode(@RequestBody RegisterReq registerReq) {
//        registerReq.setType(10);//设置为解绑
//        return registerLoginService.sendRegisterSms(registerReq,false);
//    }

//    @ApiOperation("钱包绑定交易所验证码")
//    @PostMapping("getBindCode")
//    public ResponseResult getBindCode(@RequestBody RegisterReq registerReq) {
//        registerReq.setType(9);//设置为绑定
//        return registerLoginService.sendRegisterSms(registerReq,false);
//    }
}

