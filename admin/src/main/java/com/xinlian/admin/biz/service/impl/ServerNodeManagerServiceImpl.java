package com.xinlian.admin.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.admin.biz.redis.RedisClient;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.ServerNodeManagerService;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.exception.ReqParamException;
import com.xinlian.common.request.ServerNodeReq;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.response.ServerNodeRes;
import com.xinlian.common.result.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lt
 */
@Service
@Slf4j
public class ServerNodeManagerServiceImpl implements ServerNodeManagerService {

    @Autowired
    private TServerNodeMapper serverNodeMapper;

    @Autowired
    private RedisClient redisClient;

    /**
     * 添加一级节点信息方法抽取
     * @param serverNode 新增节点的信息
     * @return 新增的结果
     */
    private ResponseResult insertServerNode(TServerNode serverNode) {
        //节点激活金额保留四位小时
        serverNode.setActiveRequireMoney(new BigDecimal(String.valueOf(serverNode.getActiveRequireMoney())).setScale(4, BigDecimal.ROUND_DOWN));
        Integer insertResult = serverNodeMapper.insert(serverNode);
        if (0 == insertResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息添加失败").build();
        }
        TServerNode tServerNode = new TServerNode();
        //添加后再获取到的id为新主键id
        tServerNode.setId(serverNode.getId());
        //节点下级id，默认自身id添加进去
        tServerNode.setChildIds(serverNode.getId().toString());
        Integer updateResult = serverNodeMapper.updateById(tServerNode);
        if (0 == updateResult) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("新节点信息修改失败").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).build();
    }

    /**
     * 根据排序字段排序展示上下级节点关系
     * @return 排序展示的结果
     */
    private List<ServerNodeRes> findNodeResList() {
        List<ServerNodeRes> redisNodeResAssemblyList = redisClient.get(RedisConstant.NODE_ALL_KEY);
        if (null != redisNodeResAssemblyList && redisNodeResAssemblyList.size() > 0) {
            return redisNodeResAssemblyList;
        }
        //一级节点列表
        List<ServerNodeRes> nodeResList = new ArrayList<>();
        //二级节点列表
        List<ServerNodeRes> nodeResFirstList = new ArrayList<>();
        //三级节点列表
        List<ServerNodeRes> nodeResSecondList = new ArrayList<>();
        //获取节点的全部信息
        List<TServerNode> nodeList = serverNodeMapper.selectList(null);
        //分级组合节点信息列表
        //一级节点信息
        nodeList.stream().filter(node -> 0 == node.getParentId()).forEach(node -> nodeResList.add(node.serverNodeRes()));
        //二级节点信息
        nodeList.stream().filter(node -> 2 == node.getParentIds().split(",").length).forEach(node -> nodeResFirstList.add(node.serverNodeRes()));
        //三级节点信息
        nodeList.stream().filter(node -> 3 == node.getParentIds().split(",").length).forEach(node -> nodeResSecondList.add(node.serverNodeRes()));
        //三级节点信息找到对应的二级节点信息添加进去
        for (ServerNodeRes nodeResFirst : nodeResFirstList) {
            List<ServerNodeRes> serverNodeResList =
                    nodeResSecondList.stream().filter(node -> node.getParentId().equals(nodeResFirst.getId())).collect(Collectors.toList());
            //三级节点信息列表升序后再添加到二级的next属性中
            nodeResFirst.setNext(serverNodeResList.stream().sorted(Comparator.comparing(ServerNodeRes::getDisplayOrder)).collect(Collectors.toList()));
        }
        //二级节点信息找到对应的一级节点信息添加进去（因为先写了三级添加到二级，所以二级添加到一级时已经携带了完整的二三级节点信息）
        for (ServerNodeRes serverNodeRes : nodeResList) {
            List<ServerNodeRes> serverNodeResList =
                    nodeResFirstList.stream().filter(node -> node.getParentId().equals(serverNodeRes.getId())).collect(Collectors.toList());
            //二级节点信息列表升序后再添加到一级的next属性中
            serverNodeRes.setNext(serverNodeResList.stream().sorted(Comparator.comparing(ServerNodeRes::getDisplayOrder)).collect(Collectors.toList()));
        }
        List<ServerNodeRes> resList = nodeResList.stream().sorted(Comparator.comparing(ServerNodeRes::getDisplayOrder)).collect(Collectors.toList());
        redisClient.set(RedisConstant.NODE_ALL_KEY,resList);
        return resList;
    }

    @Override
    public ResponseResult findNodeListByStatus(ServerNodeReq serverNodeReq) {
        //获取需要返回哪些节点信息的状态码
        Integer node_type = serverNodeReq.getNode_type();
        //查询节点所有信息
        List<ServerNodeRes> nodeResList = findNodeResList();
        try {//输入错误参数key时，抓取异常，处理代码报错
            //根据状态码进行信息筛选并返回
            if (0 == node_type) {
                //0:返回所有节点信息
                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_ALL_KEY);
                if (null == redisNodeResList || redisNodeResList.size() == 0) {
                    redisClient.set(RedisConstant.NODE_ALL_KEY,nodeResList);
                }
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_ALL_KEY)).build();
            }else if (1 == node_type){
                //1：只返回新大陆及其子节点的信息
                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_XINDALU_KEY);
                if (null == redisNodeResList || redisNodeResList.size() == 0) {
                    List<ServerNodeRes> serverNodeResList = new ArrayList<>();
                    for (ServerNodeRes serverNodeRes : nodeResList) {
                        if (7 == serverNodeRes.getId()) {
                            serverNodeResList.add(serverNodeRes);
                        }
                    }
                    redisClient.set(RedisConstant.NODE_XINDALU_KEY,serverNodeResList);
                }
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_XINDALU_KEY)).build();
            }else if (2 == node_type){
                //2：返回除新大陆及其子节点以外的节点信息
                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_OTHER_KEY);
                if (null == redisNodeResList || redisNodeResList.size() == 0) {
                    List<ServerNodeRes> serverNodeResList = new ArrayList<>();
                    for (ServerNodeRes serverNodeRes : nodeResList) {
                        if (7 != serverNodeRes.getId()) {
                            serverNodeResList.add(serverNodeRes);
                        }
                    }
                    redisClient.set(RedisConstant.NODE_OTHER_KEY,serverNodeResList);
                }
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_OTHER_KEY)).build();
            }else {
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("网络波动").build();
            }
        }catch (Exception e){
            throw new ReqParamException();
        }

    }

    @Override
    public ResponseResult findNodeListByPid(ServerNodeReq serverNodeReq) {
        if (null == serverNodeReq.getParentId()){
            serverNodeReq.setParentId(0L);
        }
        List<TServerNode> list = serverNodeMapper.selectList(new EntityWrapper<TServerNode>().eq("parent_id", serverNodeReq.getParentId()));
        if (null == list || list.size() == 0) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("请求参数不合法").build();
        }
        //节点信息列表根据排序字段升序排列
        List<TServerNode> serverNodeList = list.stream().sorted(Comparator.comparing(TServerNode::getDisplayOrder)).collect(Collectors.toList());

        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(serverNodeList).build();
    }

    @Override
    public ResponseResult findNodeById(ServerNodeReq serverNodeReq) {
        TServerNode node = serverNodeMapper.selectById(serverNodeReq.getId());
        if (null == node) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("请求参数不合法").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(node).build();
    }

    @Override
    @Transactional
    public ResponseResult createNode(TServerNode serverNode) {
        //添加一级节点信息
        if (0 == serverNode.getParentId()) {
            ResponseResult result = insertServerNode(serverNode);
            if (GlobalConstant.ResponseCode.PARAM_ERROR.equals(result.getCode())) {
                return ResponseResult.builder().code(result.getCode()).msg(result.getMsg()).build();
            }
        }else {
            //添加二级、三级节点信息
            //新parent_ids的值是上级节点的parent_ids拼接上级节点的id
            serverNode.setParentIds(serverNode.getParentIds() + "," + serverNode.getParentId());
            ResponseResult result = insertServerNode(serverNode);
            if (GlobalConstant.ResponseCode.PARAM_ERROR.equals(result.getCode())) {
                return ResponseResult.builder().code(result.getCode()).msg(result.getMsg()).build();
            }
            //接下来对上级、上上级（若有的话）节点信息进行修改
            //查找新增节点的上级节点
            TServerNode tServerNode = serverNodeMapper.selectById(serverNode.getParentId());
            if (null == tServerNode) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点id有误").build();
            }
            //是否有下级的属性状态改为是
            tServerNode.setChildStatus(1);
            //下级节点所有id中添加上新增的下级节点的id
            tServerNode.setChildIds(tServerNode.getChildIds() + "," + serverNode.getId());
            Integer updateResult = serverNodeMapper.updateById(tServerNode);
            if (0 == updateResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("修改上级节点信息失败").build();
            }
            //======================================================
            //根据新节点的上级节点（二级节点）的parentId，判断是否还有上上级节点（一级节点）
            if (0 != tServerNode.getParentId()) {
                //有上上级节点的情况（有一级节点的情况）
                //新增的节点的一级节点（上上级节点）
                TServerNode node = serverNodeMapper.selectById(tServerNode.getParentId());
                if (null == node) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("一级节点id有误").build();
                }
                //是否有下级的属性状态改为是
                node.setChildStatus(1);
                //下级节点所有id中添加上新增的下级节点（三级节点）的id
                node.setChildIds(node.getChildIds() + "," + serverNode.getId());
                Integer update = serverNodeMapper.updateById(node);
                if (0 == update) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("修改一级节点信息失败").build();
                }
            }
        }
        //删除缓存
        for (String key : redisClient.getKeys("NODE_*")) {
            redisClient.deleteByKey(key);
        }
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "WITHDRAW_SERVER_NODE" + "*")) {
            redisClient.deleteByKey(key);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("节点信息添加成功").build();
    }

    @Override
    public ResponseResult updateNode(TServerNode serverNode) {
        log.info(JSON.toJSONString(serverNode));
        if (null != serverNode.getActiveRequireMoney()) {
            serverNode.setActiveRequireMoney(new BigDecimal(String.valueOf(serverNode.getActiveRequireMoney())).setScale(4, BigDecimal.ROUND_DOWN));
        }
        Integer updateResult = serverNodeMapper.update(serverNode,new EntityWrapper<TServerNode>().eq("id",serverNode.getId()));
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息修改失败").build();
        }
        //删除缓存
        for (String key : redisClient.getKeys("NODE_*")) {
            redisClient.deleteByKey(key);
        }
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "WITHDRAW_SERVER_NODE" + "*")) {
            redisClient.deleteByKey(key);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("节点信息修改成功").build();
    }

    @Override
    public ResponseResult updateClansmanAndChildNode(TServerNode serverNode) {
        log.info(JSON.toJSONString(serverNode));
        //格式化填入的激活数量值
        if (null != serverNode.getActiveRequireMoney()) {
            serverNode.setActiveRequireMoney(new BigDecimal(String.valueOf(serverNode.getActiveRequireMoney())).setScale(4, BigDecimal.ROUND_DOWN));
        }
        //获取nodeId，check是否顶级节点
        if(serverNode.getParentId().intValue()!=Integer.parseInt(serverNode.getParentIds())){
            throw new BizException("不是顶级节点，请确认!");
        }
        //本身节点特殊属性，需要置空，不让去统一更新
        serverNode.setParentId(null);
        serverNode.setParentIds(null);
        serverNode.setName(null);
        serverNode.setNickname(null);
        serverNode.setLogoUrl(null);
        serverNode.setChildStatus(null);
        serverNode.setHiddenStatus(null);
        serverNode.setChildIds(null);
        serverNode.setDisplayOrder(null);
        Integer updateResult = serverNodeMapper.update(serverNode,new EntityWrapper<TServerNode>().like("parent_ids","0,".concat(serverNode.getId().toString()), SqlLike.RIGHT));
        if (0 == updateResult) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息修改失败").build();
        }
        //批量删除缓存
        for (String key : redisClient.getKeys("NODE_*")) {
            redisClient.deleteByKey(key);
        }
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "WITHDRAW_SERVER_NODE" + "*")) {
            redisClient.deleteByKey(key);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("节点信息修改成功").build();
    }

    @Override
    @Transactional
    public ResponseResult deleteNode(ServerNodeReq serverNodeReq) {
        //根据节点id是否能查询到要删除的节点（节点id是否有数据）
        TServerNode node = serverNodeMapper.selectById(serverNodeReq.getId());
        if (null == node) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("请求参数不合法").build();
        }
        //判断删除的节点是几级节点
        //删除一级节点
        if (0 == node.getParentId()) {
            //该节点的childIds
            Long[] nodeIds = (Long[]) ConvertUtils.convert(node.getChildIds().split(","),Long[].class);
            for (Long nodeId : nodeIds) {
                Integer deleteResult = serverNodeMapper.deleteById(nodeId);
                if (0 == deleteResult) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息删除失败").build();
                }
            }
        }
        //删除二级节点，即删除二级节点childIds中的节点，并修改一级节点的childIds
        if (2 == node.getParentIds().split(",").length) {
            //该节点的childIds
            Long[] nodeIds = (Long[]) ConvertUtils.convert(node.getChildIds().split(","),Long[].class);
            //根据parentId获取一级节点信息
            TServerNode parentNode = serverNodeMapper.selectById(node.getParentId());
            //获取一级节点的childIds数组
            Long[] parentNodeIds = (Long[]) ConvertUtils.convert(parentNode.getChildIds().split(","),Long[].class);
            //一级节点的childIds数组转换成集合
            List<Long> parentNodeIdList = Arrays.stream(parentNodeIds).collect(Collectors.toList());
            //删除二级节点及其子节点，同时筛选在删除节点范围的id从list中删除
            for (Long nodeId : nodeIds) {
                Integer deleteResult = serverNodeMapper.deleteById(nodeId);
                if (0 == deleteResult) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息删除失败").build();
                }
                //在删除范围的节点id从一级节点childIds的list中删除
                parentNodeIdList.removeIf(nodeId::equals);
            }
            if (1 == parentNodeIdList.size()) {
                //如果删除后上级节点没有子节点的情况
                parentNode.setChildStatus(0);
                parentNode.setChildIds(parentNodeIdList.get(0).toString());
                Integer integer = serverNodeMapper.updateById(parentNode);
                if (0 == integer) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点中关于子节点的信息修改失败").build();
                }
            }else {
                //删除后上级节点还有子节点的情况
                //集合用逗号拼接成字符串
                String childIds = StringUtils.join(parentNodeIdList, ",");
                //删完之后重新赋值
                parentNode.setChildIds(childIds);
                Integer integer = serverNodeMapper.updateById(parentNode);
                if (0 == integer) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点中关于子节点的信息修改失败").build();
                }
            }
        }
        //删除三级节点，即删除后将三级节点的id从二级、一级节点的childIds中删除
        if (3 == node.getParentIds().split(",").length) {
            //删除三级节点
            Integer deleteResult = serverNodeMapper.deleteById(node.getId());
            if (0 == deleteResult) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("节点信息删除失败").build();
            }
            //获取二级节点
            TServerNode serverNode = serverNodeMapper.selectById(node.getParentId());
            //获取二级节点的childIds数组
            Long[] parentNodeIds = (Long[]) ConvertUtils.convert(serverNode.getChildIds().split(","),Long[].class);
            //二级节点的childIds数组转换成集合
            List<Long> parentNodeIdList = Arrays.stream(parentNodeIds).collect(Collectors.toList());
            //将三级节点的id从二级节点的childIds中删除
            parentNodeIdList.removeIf(node.getId()::equals);
            if (1 == parentNodeIdList.size()) {
                //如果删除后二级节点没有子节点的情况
                serverNode.setChildStatus(0);
                serverNode.setChildIds(parentNodeIdList.get(0).toString());
                Integer integer = serverNodeMapper.updateById(serverNode);
                if (0 == integer) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点中关于子节点的信息修改失败").build();
                }
            }else {
                //删除后上级节点还有子节点的情况
                //集合用逗号拼接成字符串
                String childIds = StringUtils.join(parentNodeIdList, ",");
                //删完之后重新赋值
                serverNode.setChildIds(childIds);
                Integer integer = serverNodeMapper.updateById(serverNode);
                if (0 == integer) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点中关于子节点的信息修改失败").build();
                }
            }

            //获取一级节点
            TServerNode firstNode = serverNodeMapper.selectById(serverNode.getParentId());
            //获取一级节点的childIds数组
            Long[] firstNodeIds = (Long[]) ConvertUtils.convert(firstNode.getChildIds().split(","),Long[].class);
            //一级节点的childIds数组转换成集合
            List<Long> firstNodeIdList = Arrays.stream(firstNodeIds).collect(Collectors.toList());
            //将三级节点的id从一级节点的childIds中删除
            firstNodeIdList.removeIf(node.getId()::equals);
            //集合用逗号拼接成字符串
            String firstChildIds = StringUtils.join(firstNodeIdList, ",");
            //删完之后重新赋值
            firstNode.setChildIds(firstChildIds);
            Integer integer = serverNodeMapper.updateById(firstNode);
            if (0 == integer) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("上级节点中关于子节点的信息修改失败").build();
            }
        }
        //删除缓存
        for (String key : redisClient.getKeys("NODE_*")) {
            redisClient.deleteByKey(key);
        }
        for (String key : redisClient.getKeys(RedisConstant.APP_REDIS_PREFIX + "WITHDRAW_SERVER_NODE" + "*")) {
            redisClient.deleteByKey(key);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).msg("节点信息删除成功").build();
    }
}