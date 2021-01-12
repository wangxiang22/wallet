package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.ResponseResult;

public interface ServerNodeManagerService {

    /**
     * 根据状态码返回相应节点信息
     * @param serverNodeReq
     * @return
     */
    ResponseResult findNodeListByStatus(ServerNodeReq serverNodeReq);
    /**
     * 根据父节点id查询节点信息
     * @param serverNodeReq
     * @return
     */
    ResponseResult findNodeListByPid(ServerNodeReq serverNodeReq);

    /**
     * 根据节点id展示该节点的详情
     * @param serverNodeReq
     * @return
     */
    ResponseResult findNodeById(ServerNodeReq serverNodeReq);

    /**
     * 新增节点
     * @param serverNode
     * @return
     */
    ResponseResult createNode(TServerNode serverNode);

    /**
     * 修改节点
     * @param serverNode
     * @return
     */
    ResponseResult updateNode(TServerNode serverNode);

    /**
     * 修改同宗及以下节点
     * @param serverNode
     */
    ResponseResult updateClansmanAndChildNode(TServerNode serverNode);

    /**
     * 根据节点id删除该节点（如有子节点，也一并删除）
     * @param serverNodeReq
     * @return
     */
    ResponseResult deleteNode(ServerNodeReq serverNodeReq);
}
