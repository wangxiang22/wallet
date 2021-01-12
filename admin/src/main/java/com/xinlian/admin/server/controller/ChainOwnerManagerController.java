package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.ChainOwnerManagerService;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.ResponseResultPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 链权人信息管理接口
 */
@Api(value = "链权人信息管理接口")
@RestController
@RequestMapping(value = "/chainOwnerManager")
public class ChainOwnerManagerController {
    @Autowired
    private ChainOwnerManagerService chainOwnerManagerService;

    @ApiOperation(value = "根据搜索条件查询链权人信息（分页）",httpMethod = "GET")
    @GetMapping("/findListPage")
    public ResponseResultPage findListPage(
            @ApiParam(name = "paramMap", value = "pageNum:当前页\n," +
                    "pageSize:每页显示条数\n," +
                    "userName:用户名\n," +
                    "uid:用户id\n," +
                    "mobile:手机号码\n," +
                    "authSn:用户实名证件号\n," +
                    "signStatus:用户签约链权人状态 - 0：未签约，1：已签约\n," +
                    "nodeId:节点id\n")
            @RequestParam Map<String,Object> paramMap) {
        try {
            return new ResponseResultPage(chainOwnerManagerService.queryPage(paramMap));
        }catch (Exception e){
            return new ResponseResultPage(false);
        }
    }

    @ApiOperation(value = "节点分组查询链权人人数",httpMethod = "POST")
    @PostMapping("/findNodeChainOwnerNumList")
    public ResponseResult findNodeChainOwnerNumList() {
        return chainOwnerManagerService.findNodeChainOwnerNumList();
    }
}
