package com.xinlian.admin.biz.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.service.INodeService;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.NodeDicRes;
import com.xinlian.common.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NodeServiceImpl implements INodeService {
    @Autowired
    private TServerNodeMapper serverNodeMapper;

    @Override
    public ResponseResult<List<NodeDicRes>> findNodeDic(ServerNodeReq serverNodeReq){
        ResponseResult<List<NodeDicRes>> result = new ResponseResult<>();
        List<TServerNode> list = serverNodeMapper.selectList(new EntityWrapper<TServerNode>()
                .eq("hidden_status", 1));
        List<NodeDicRes> resList = new ArrayList<>(list.size());
        list.stream().forEach(e -> {
            resList.add(e.nodeDicRes());
        });
        //组装数据
        List<NodeDicRes> topList = new ArrayList<>();
        Map<Long, List<NodeDicRes>> map = resList.stream().collect(Collectors.groupingBy(NodeDicRes::getParentId));
        for(NodeDicRes res : resList){
            res.setChildes(map.get(res.getNodeId()));
            if(res.getParentId().longValue() == 0){
                if(serverNodeReq.getNode_type().intValue() == 0){
                    //所以节点
                    topList.add(res);
                }else if(serverNodeReq.getNode_type().intValue() == 1){
                    //除去新大陆节点
                    if(!res.getName().equals("新大陆")){
                        topList.add(res);
                    }
                }else {
                    //新大陆节点
                    if(res.getName().equals("新大陆")){
                        topList.add(res);
                    }
                }
            }
        }
        result.responseResult(GlobalConstant.ResponseCode.SUCCESS, topList);
        return result;
    }

}
