package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.jwt.util.JwtUtil;
import com.xinlian.admin.biz.service.UserInfoService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.*;
import com.xinlian.common.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "会员管理")
@Controller
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "会员列表", httpMethod = "POST")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public PageResult<List<UserInfoRes>> userInfoList(@RequestBody UserListReq req){
        return userInfoService.userInfoList(req);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_FREEZE_MANAGE,typeName = OperationTypeEnum.USER_FREEZE, opeDesc = "冻结会员")
    @ApiOperation(value = "冻结会员", httpMethod = "POST")
    @RequestMapping(value = "/freeze", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult freezeUser(HttpServletRequest request, @RequestBody FreezeUserReq req){
        req.setUid(jwtUtil.getUserId(request));
        return userInfoService.freezeUser(req);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_FREEZE_MANAGE,typeName = OperationTypeEnum.USER_FREEZE, opeDesc = "解冻会员")
    @ApiOperation(value = "解冻 会员", httpMethod = "POST")
    @RequestMapping(value = "/unfreeze", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult unfreezeUser(HttpServletRequest request, @RequestBody FreezeUserReq req){
        req.setUid(jwtUtil.getUserId(request));
        return userInfoService.unfreezeUser(req);
    }

    @ApiOperation(value = "会员信息详情", httpMethod = "POST")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<UserInfoDetailRes> userInfoDetail(@RequestBody IdReq req){
        return userInfoService.userInfoDetail(req.getUid());
    }

    @OpeAnnotation(modelName = OperationModuleEnum.CUSTOMER_INFO_MANAGE,typeName = OperationTypeEnum.OTHER_OPERATE, opeDesc = "修改客户信息")
    @ApiOperation(value = "修改用户信息", httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<UserInfoDetailRes> userInfoDetail(HttpServletRequest request, @RequestBody UpdUserInfoReq req){
        return new ResponseResult(true);//userInfoService.updateUser(req, jwtUtil.getUserId(request));
    }

    @ApiOperation(value = "分页查询用户信息",httpMethod = "POST")
    @PostMapping("/queryUserListPage")
    @ResponseBody
    public PageResult<List<UserInfoManagerRes>> queryUserListPage(@RequestBody UserInfoManagerReq userInfoManagerReq) {
        return userInfoService.queryUserListPage(userInfoManagerReq);
    }

    @ApiOperation(value = "查询用户上下级",httpMethod = "POST")
    @PostMapping("/queryUserInvitation")
    @ResponseBody
    public ResponseResult<UserInfoInvitationRes> queryUserInvitation(@ApiParam(name = "uid",value = "用户id",required = true)
                                                                         @RequestParam Long uid) {
        return userInfoService.queryUserInvitation(uid);
    }

    @ApiOperation(value = "通过钱包地址查询用户信息",httpMethod = "POST")
    @PostMapping("/queryUserByWallet")
    @ResponseBody
    public ResponseResult<WalletFindUserRes> queryUserByWallet(
            @ApiParam(name = "addressType",value = "地址类型 - 0：旧优盾地址，1：新钱包地址",required = true) @RequestParam int addressType,
            @ApiParam(name = "currencyAddress",value = "钱包地址",required = true) @RequestParam String currencyAddress) {
        return userInfoService.queryUserByWallet(addressType,currencyAddress);
    }
}
