package com.xinlian.admin.server.controller;


import com.xinlian.admin.biz.service.TPayPwdChangeService;
import com.xinlian.biz.model.TPayPwdChange;
import com.xinlian.common.dto.PayPwdChangeDto;
import com.xinlian.common.request.CheckUserAuthReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 支付密码变更表（用户手机号已不可用） 前端控制器
 * </p>
 *
 * @author lx
 * @since 2020-06-04
 */
@RestController
@RequestMapping("/tPayPwdChange")
public class TPayPwdChangeController {
    @Autowired
    private TPayPwdChangeService tPayPwdChangeService;

    @ApiOperation("查看申请列表")
    @PostMapping("queryList")
    public PageResult<List<PayPwdChangeDto>> queryList(@RequestBody CheckUserAuthReq checkUserAuthReq){
        return tPayPwdChangeService.queryList(checkUserAuthReq);
    }

    @ApiOperation("通过/拒绝")
    @PostMapping("passOrRefuse")
    public ResponseResult passOrRefuse(@RequestBody TPayPwdChange tPayPwdChange){
        return tPayPwdChangeService.passOrRefuse(tPayPwdChange);
    }

}

