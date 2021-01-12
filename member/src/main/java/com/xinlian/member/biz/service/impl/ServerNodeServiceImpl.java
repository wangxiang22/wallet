package com.xinlian.member.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.xinlian.biz.dao.TServerNodeMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.contants.GlobalConstant;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.enums.ServerNodeWithdrawStatusEnum;
import com.xinlian.common.exception.ReqParamException;
import com.xinlian.common.request.NodeCurrencyReq;
import com.xinlian.common.response.NodeCurrencyRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.IServerNodeService;
import com.xinlian.member.biz.service.WithdrawCustomerService;
import com.xinlian.member.server.vo.ServerNodeVo;
import com.xinlian.member.server.vo.ServerNodeVoConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ServerNodeServiceImpl implements IServerNodeService {

    @Autowired
    private TServerNodeMapper serverNodeMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private WithdrawCustomerService withdrawCustomerService;
    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    /**
     * 根据排序字段排序展示上下级节点关系
     * @return
     */
    private List<ServerNodeVo> findNodeResList() {
        List<ServerNodeVo> redisNodeResAssemblyList = redisClient.get(RedisConstant.NODE_ASSEMBLY);
        if(null==redisNodeResAssemblyList || redisNodeResAssemblyList.size()==0){
            //获取节点的全部信息
            List<TServerNode> nodeList = serverNodeMapper.selectList(new EntityWrapper<TServerNode>()
                    .orderBy("display_order"));
            redisNodeResAssemblyList = packageServerNodeData(nodeList);
            redisClient.set(RedisConstant.NODE_ASSEMBLY,redisNodeResAssemblyList);
        }
        return redisNodeResAssemblyList;
    }

    /**
     * 组装节点 - 只需要父节点是0的数据
     * @param objectList
     */
    private List<ServerNodeVo> packageServerNodeData(List<TServerNode> objectList) {
        List<ServerNodeVo> parentList = new ArrayList<>();
        objectList.forEach(serverNode ->{
            if(serverNode.getParentId().intValue()==0) {
                parentList.add(new ServerNodeVoConvertor().convert(serverNode));
            }
        });
        // 为顶级节点设置子级节点，getChild是递归调用的
        for (ServerNodeVo serverNodeVo : parentList) {
            serverNodeVo.setNext(getChild(serverNodeVo.getId(), objectList));
        }
        return parentList;
    }


    /**
     * 递归查找子节点
     * @param serverNodeId 当前节点id
     * @param serverNodes 所有的列表
     * @return
     */
    private List<ServerNodeVo> getChild(Long serverNodeId, List<TServerNode> serverNodes) {
        // 子节点
        List<ServerNodeVo> childList = new ArrayList<>();
        for (TServerNode serverNode : serverNodes) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (serverNode.getParentId().equals(serverNodeId)) {
                childList.add(new ServerNodeVoConvertor().convert(serverNode));
            }
        }
        // 把子节点的子节点再循环一遍
        for (ServerNodeVo serverNode : childList) {
            // 递归
            serverNode.setNext(getChild(serverNode.getId(), serverNodes));
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }


    @Override
    public ResponseResult findNodeListByStatus() {
        try {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(findNodeResList()).build();
        }catch (Exception e){
            log.error("获取节点信息失败：{}" , e.toString(),e);
            throw new ReqParamException();
        }
    }

//    @Override
//    public ResponseResult findNodeListByStatus(ServerNodeReq serverNodeReq) {
//        //获取需要返回哪些节点信息的状态码
//        Integer node_type = serverNodeReq.getNode_type();
//        //查询节点所有信息
//        List<ServerNodeRes> nodeResList = findNodeResList();
//        try {//输入错误参数key时，抓取异常，处理代码报错
//            //根据状态码进行信息筛选并返回
//            if (0 == node_type) {   //0:返回所有节点信息
//                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_ALL_KEY);
//                if (null == redisNodeResList || redisNodeResList.size() == 0) {
//                    redisClient.set(RedisConstant.NODE_ALL_KEY,nodeResList);
//                }
//                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_ALL_KEY)).build();
//            }else if (1 == node_type){  //1：只返回新大陆及其子节点的信息
//                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_XINDALU_KEY);
//                if (null == redisNodeResList || redisNodeResList.size() == 0) {
//                    List<ServerNodeRes> serverNodeResList = new ArrayList<>();
//                    for (ServerNodeRes serverNodeRes : nodeResList) {
//                        if (7 == serverNodeRes.getId()) {
//                            serverNodeResList.add(serverNodeRes);
//                        }
//                    }
//                    redisClient.set(RedisConstant.NODE_XINDALU_KEY,serverNodeResList);
//                }
//                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_XINDALU_KEY)).build();
//            }else if (2 == node_type){  //2：返回除新大陆及其子节点以外的节点信息
//                List<ServerNodeRes> redisNodeResList = redisClient.get(RedisConstant.NODE_OTHER_KEY);
//                if (null == redisNodeResList || redisNodeResList.size() == 0) {
//                    List<ServerNodeRes> serverNodeResList = new ArrayList<>();
//                    for (ServerNodeRes serverNodeRes : nodeResList) {
//                        if (7 != serverNodeRes.getId()) {
//                            serverNodeResList.add(serverNodeRes);
//                        }
//                    }
//                    redisClient.set(RedisConstant.NODE_OTHER_KEY,serverNodeResList);
//                }
//                return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(redisClient.get(RedisConstant.NODE_OTHER_KEY)).build();
//            }else {
//                return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("网络波动").build();
//            }
//        }catch (Exception e){
//            log.error("获取节点信息失败：{}" , e.toString(),e);
//            throw new ReqParamException();
//        }
//    }

    @Override
    public ResponseResult findUserNode(Long id) {
        TServerNode tServerNode = this.getById(id);
        if (null == tServerNode) {
            return ResponseResult.builder().code(GlobalConstant.ResponseCode.PARAM_ERROR).msg("网络波动").build();
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(tServerNode.nodeRes()).build();
    }

    @Override
    public ResponseResult findRechargeAndCashStatus(NodeCurrencyReq nodeCurrencyReq) {
        TServerNode node = serverNodeMapper.selectById(nodeCurrencyReq.getNodeId());
        Integer coinId = nodeCurrencyReq.getCoinId();
        NodeCurrencyRes nodeCurrencyRes = new NodeCurrencyRes();
        if (CurrencyEnum.USDT.getCurrencyId() == coinId) {
            nodeCurrencyRes.setRechargeStatus(node.getRechargeUsdtStatus());
            //判断是否为特殊用户，特殊用户可无视开关进行提现
            if ( this.checkIsSeaPatrolByFromServerNode(nodeCurrencyReq.getNodeId())
                    ||withdrawCustomerService.checkWithdrawCustomerUid(nodeCurrencyReq.getUid())) {
                nodeCurrencyRes.setCashStatus(ServerNodeWithdrawStatusEnum.WITHDRAW_YES.getCode());
            }else {
                nodeCurrencyRes.setCashStatus(node.getCashUsdtStatus());
            }
        }else if (CurrencyEnum.CAT.getCurrencyId() == coinId){
            nodeCurrencyRes.setRechargeStatus(node.getRechargeCatStatus());
            nodeCurrencyRes.setCashStatus(node.getCashCatStatus());
        }else if (CurrencyEnum.CAG.getCurrencyId() == coinId) {
            nodeCurrencyRes.setRechargeStatus(node.getRechargeCagStatus());
            nodeCurrencyRes.setCashStatus(node.getCashCagStatus());
        }else if(CurrencyEnum.GPT.getCurrencyId() == coinId){
            nodeCurrencyRes.setRechargeStatus(node.getRechargeGptStatus());
            nodeCurrencyRes.setCashStatus(node.getCashGptStatus());
        }else {
            log.error("~~~~~~findRechargeAndCashStatus:{}","传得币种id不存在"+coinId);
            nodeCurrencyRes.setRechargeStatus(0);
            nodeCurrencyRes.setCashStatus(0);
        }
        return ResponseResult.builder().code(GlobalConstant.ResponseCode.SUCCESS).result(nodeCurrencyRes).build();
    }

    private boolean checkIsSeaPatrolByFromServerNode(Long fromServerNodeId){
        if(!nodeVoyageUtil.belongVoyageNode(fromServerNodeId)){
            return false;
        }
        //check is open time
        String openEndTime = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SEA_PATROL_TRADE_END_TIME.getBelongsSystemCode());
        String openStartTime = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SEA_PATROL_TRADE_START_TIME.getBelongsSystemCode());
        //参数未配置，截止时间小于当前时间
        if(StringUtils.isEmpty(openEndTime) || StringUtils.isEmpty(openStartTime)
                ||  System.currentTimeMillis() < Long.parseLong(openStartTime) || System.currentTimeMillis()> Long.parseLong(openEndTime)){
            return false;
        }
        return true;
    }

    @Override
    public TServerNode getServerNodeByWithdrawAddress(TWalletInfo walletInfo){
        return serverNodeMapper.getServerNodeByWithdrawAddress(walletInfo);
    }

    @Override
	public TServerNode getServerNodeByWalletInfoUid(Long uId) {
		return serverNodeMapper.getServerNodeByWalletInfoUid(uId);
	}

    @Override
	public TServerNode getById(@NotNull Long serverNodeId) {
		if (null == serverNodeId) {
			return null;
		}
		try {
			String serverNodeSuffix = "WITHDRAW_SERVER_NODE" + serverNodeId;
			String redisKey = RedisConstant.APP_REDIS_PREFIX + serverNodeSuffix;
			String redisValue = redisClient.get(redisKey);
			if (StringUtils.isBlank(redisValue)) {
				TServerNode serverNode = serverNodeMapper.selectById(serverNodeId);
				if (null != serverNode) {
					redisClient.set(redisKey, JSONObject.toJSONString(serverNode));
				}
				return serverNode;
			} else {
				return JSONObject.parseObject(redisValue, TServerNode.class);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
