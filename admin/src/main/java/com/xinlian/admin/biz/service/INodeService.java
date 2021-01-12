package com.xinlian.admin.biz.service;

import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.NodeDicRes;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface INodeService {

    ResponseResult<List<NodeDicRes>> findNodeDic(ServerNodeReq serverNodeReq);

}
