package com.xinlian.admin.biz.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.TChainOwnerMapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TChainOwner;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.response.ChainOwnerNumberRes;
import com.xinlian.common.response.ResponseResult;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChainOwnerManagerService extends PageBaseService<TChainOwner> {
    @Autowired
    private TChainOwnerMapper chainOwnerMapper;
    @Autowired
    private TServerNodeMapper serverNodeMapper;

    /**
     * 根据搜索条件查询链权人信息（分页）
     * @param tChainOwner
     * @return
     * @throws Exception
     */
    @Override
    public List<TChainOwner> query(TChainOwner tChainOwner) throws Exception {
        return chainOwnerMapper.query(tChainOwner);
    }

    /**
     * 节点分组查询链权人人数
     *      只展示一级节点，人数信息包含子节点的
     */
    public ResponseResult findNodeChainOwnerNumList() {
        //获取一级节点列表
        List<TServerNode> firstNodeList = serverNodeMapper.selectList(new EntityWrapper<TServerNode>().eq("parent_id", 0));
        List<ChainOwnerNumberRes> list = new ArrayList<>();
        for (TServerNode serverNode : firstNodeList) {
            ChainOwnerNumberRes chainOwnerNumberRes = new ChainOwnerNumberRes();
            chainOwnerNumberRes.setNodeId(serverNode.getId());
            chainOwnerNumberRes.setNodeName(serverNode.getName());
            List<Long> childNodeIdList = getChildNodeIdList(serverNode.getId());
            if (null == childNodeIdList) {
                Integer count = chainOwnerMapper.selectCount(new EntityWrapper<TChainOwner>().eq("node_id", serverNode.getId()));
                chainOwnerNumberRes.setNodeChainOwnerNum(Long.valueOf(count));
            }else {
                List<Long> nodeIdList = new ArrayList<>();
                nodeIdList.add(serverNode.getId());
                nodeIdList.addAll(childNodeIdList);
                Integer count = chainOwnerMapper.selectCount(new EntityWrapper<TChainOwner>().in("node_id", nodeIdList));
                chainOwnerNumberRes.setNodeChainOwnerNum(Long.valueOf(count));
            }
            list.add(chainOwnerNumberRes);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(list).build();
    }

    /**
     * 根据一级节点id获取其子节点（二级+三级）的id列表
     */
    private List<Long> getChildNodeIdList(Long nodeId) {
        TServerNode tServerNode = serverNodeMapper.selectById(nodeId);
        if (0 == tServerNode.getChildStatus()) {
            return null;
        }
        Long[] childList = (Long[]) ConvertUtils.convert(tServerNode.getChildIds().split(","),Long.class);
        List<Long> list = Arrays.stream(childList).collect(Collectors.toList());
        list.removeIf(nodeId::equals);
        return list;
    }
}
