package com.xinlian.member.server.controller;

import com.xinlian.common.request.AddressPoolReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.biz.model.TAddressPool;
import com.xinlian.member.biz.service.IAddressPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Api(value = "地址")
@Controller
@RequestMapping("/{versionPath}/address")
public class AddressPoolControl {

    @Autowired
    private IAddressPoolService addressPoolService;

    @ApiOperation(value = "地址列表", httpMethod = "POST")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<List<TAddressPool>> findTAddressPool(@RequestBody AddressPoolReq req){
        return addressPoolService.findTAddressPool(req.getStatus());
    }

}
