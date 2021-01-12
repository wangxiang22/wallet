package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.INodeService;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.NodeDicRes;
import com.xinlian.common.response.ResponseResult;
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
@RequestMapping("/test")
public class TestController {

    @Autowired
    private INodeService nodeService;

    @ApiOperation(value = "节点 列表", httpMethod = "POST")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult<List<NodeDicRes>> findNodeDic(@RequestBody ServerNodeReq req){
        return nodeService.findNodeDic(req);
    }

}
