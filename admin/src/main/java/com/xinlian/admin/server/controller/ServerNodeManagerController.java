package com.xinlian.admin.server.controller;

import com.xinlian.admin.biz.service.ServerNodeManagerService;
import com.xinlian.admin.server.operationLog.OpeAnnotation;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.enums.OperationModuleEnum;
import com.xinlian.common.enums.OperationTypeEnum;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 节点信息管理接口
 */
@Api(value = "节点信息管理接口")
@RestController
@RequestMapping(value = "/serverNodeManager")
public class ServerNodeManagerController {

    @Autowired
    private ServerNodeManagerService serverNodeManagerService;

    @ApiOperation(value = "根据状态码返回相应节点信息",httpMethod = "POST")
    @PostMapping("/findNodeListByStatus")
    public ResponseResult findNodeListByStatus(@RequestBody ServerNodeReq serverNodeReq) {
        return serverNodeManagerService.findNodeListByStatus(serverNodeReq);
    }

    @ApiOperation(value = "查询一级节点信息",httpMethod = "POST")
    @PostMapping("/findFirstNodeList")
    public ResponseResult findFirstNodeList() {
        return serverNodeManagerService.findNodeListByPid(new ServerNodeReq());
    }

    @ApiOperation(value = "根据父节点id查询节点信息",httpMethod = "POST")
    @PostMapping("/findNodeListByPid")
    public ResponseResult findNodeListByPid(@RequestBody ServerNodeReq serverNodeReq) {
        return serverNodeManagerService.findNodeListByPid(serverNodeReq);
    }

    @ApiOperation(value = "根据节点id展示该节点的详情",httpMethod = "POST")
    @PostMapping("/findNodeById")
    public ResponseResult findNodeById(@RequestBody ServerNodeReq serverNodeReq) {
        return serverNodeManagerService.findNodeById(serverNodeReq);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NODE_MANAGE,typeName = OperationTypeEnum.SERVER_NODE_ASSERT, opeDesc = "新增节点")
    @ApiOperation(value = "新增节点",httpMethod = "POST")
    @PostMapping("/createNode")
    public ResponseResult createNode(@RequestBody TServerNode serverNode) {
        return serverNodeManagerService.createNode(serverNode);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NODE_MANAGE,typeName = OperationTypeEnum.SERVER_NODE_ASSERT, opeDesc = "修改节点")
    @ApiOperation(value = "修改节点",httpMethod = "POST")
    @PostMapping("/updateNode")
    public ResponseResult updateNode(@RequestBody TServerNode serverNode) {
        return serverNodeManagerService.updateNode(serverNode);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NODE_MANAGE,typeName = OperationTypeEnum.SERVER_NODE_ASSERT, opeDesc = "修改跟下同宗节点")
    @ApiOperation(value = "修改同宗及以下节点",httpMethod = "POST")
    @PostMapping("/update/clansmanAndChildNode")
    public ResponseResult updateClansmanAndChildNode(@RequestBody TServerNode serverNode) {
        if(118 != serverNode.getId().intValue()){
            throw new BizException("只能大航海节点使用,请确认!");
        }
        return serverNodeManagerService.updateClansmanAndChildNode(serverNode);
    }

    @OpeAnnotation(modelName = OperationModuleEnum.NODE_MANAGE,typeName = OperationTypeEnum.SERVER_NODE_ASSERT, opeDesc = "删除节点")
    @ApiOperation(value = "根据节点id删除该节点（如有子节点，也一并删除）",httpMethod = "POST")
    @PostMapping("/deleteNode")
    public ResponseResult deleteNode(@RequestBody ServerNodeReq serverNodeReq) {
        return serverNodeManagerService.deleteNode(serverNodeReq);
    }
}
