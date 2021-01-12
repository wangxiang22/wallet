package com.xinlian.member.server.controller;

import com.xinlian.common.request.AddWithdrawAddressReq;
import com.xinlian.common.request.IdReq;
import com.xinlian.common.request.RechargeCurrencyReq;
import com.xinlian.common.response.CurrencyInfoRes;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.WithdrawAddressRes;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import com.xinlian.member.biz.service.TUserWithdrawAddressRefService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "注册登录")
@Controller
@RequestMapping("/{versionPath}/withdraw")
public class UserWithdrawAddressRefController {

    @Autowired
    private TUserWithdrawAddressRefService userWithdrawAddressRefService;

    @Autowired
    private JwtUtil jwtUtil;

    /*@ApiOperation("提币地址")
    @RequestMapping(value = "/address/all", method = RequestMethod.POST)
    public ResponseResult getUserWithdrawAddressRef() {
        try {
            Page<TUserWithdrawAddressRef> page = new Page<>();
            ResponseResult responseResult = new ResponseResult();
            responseResult.setResult(userWithdrawAddressRefService.selectPage(page));
            return responseResult;
        } catch (Exception e) {
            new RuntimeException("获取客户提币地址异常");
        }
        return null;
    }*/

    @ApiOperation(value = "用户提币地址列表", httpMethod = "POST")
    @RequestMapping(value = "/address/list", method = RequestMethod.POST)
    @ResponseBody
    public PageResult<List<WithdrawAddressRes>> withdrawAddressList(HttpServletRequest request, @RequestBody IdReq req){
        req.setUid(jwtUtil.getUserId(request));
        return userWithdrawAddressRefService.withdrawAddressList(req);
    }

    @ApiOperation(value = "添加提币地址", httpMethod = "POST")
    @RequestMapping(value = "/address/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult addAddressRef(HttpServletRequest request, @RequestBody AddWithdrawAddressReq req){
        req.setUid(jwtUtil.getUserId(request));
        boolean flag = false;
        if(!StringUtils.isEmpty(req.getCurrencyAddress())
                && req.getCurrencyAddress().length()==42
                && req.getCurrencyAddress().substring(0,2).equals("0x")){
            flag = true;
        }
        if(!StringUtils.isEmpty(req.getCurrencyAddress())
                && req.getCurrencyAddress().substring(0,1).equals("T")
                && req.getCurrencyAddress().length()==34){
            flag = true;
        }
        if(!flag){
            throw new BizException("请输入正确地址!");
        }
        return userWithdrawAddressRefService.addAddressRef(req);
    }

    @ApiOperation(value = "删除提币地址", httpMethod = "POST")
    @RequestMapping(value = "/address/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult delWithdrawAddress(HttpServletRequest request, @RequestBody IdReq req){
        req.setUid(jwtUtil.getUserId(request));
        return userWithdrawAddressRefService.delWithdrawAddress(req);
    }

    @ApiOperation(value = "币种列表", httpMethod = "POST")
    @RequestMapping(value = "/currency/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoRes(){
        return userWithdrawAddressRefService.findCurrencyInfoResNew();
    }

    @ApiOperation(value = "充值提现 币种列表", httpMethod = "POST")
    @RequestMapping(value = "/currency/recharge/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<List<CurrencyInfoRes>> findCurrencyInfoRes(@RequestBody RechargeCurrencyReq req){
        return userWithdrawAddressRefService.rechargeCurrencyRes(req);
    }
}
