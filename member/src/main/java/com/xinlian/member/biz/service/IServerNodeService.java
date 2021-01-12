package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.common.request.NodeCurrencyReq;
import com.xinlian.common.response.ResponseResult;

public interface IServerNodeService {
    /**
     * 查询所有节点信息
     * @return
     */
    ResponseResult findNodeListByStatus();

    /**
     * 根据用户的节点id返回所需节点信息
     * @param id 节点id
     * @return
     */
    ResponseResult findUserNode(Long id);

    /**
     * 根据用户节点id及币种id查找该币种的充提状态
     * @param nodeCurrencyReq
     * @return
     */
    ResponseResult findRechargeAndCashStatus(NodeCurrencyReq nodeCurrencyReq);

    /**
     * 根据币种地址获取到 转出节点服务 外部地址节点server_node_id为-1
     * @param walletInfo
     * @return
     */
    TServerNode getServerNodeByWithdrawAddress(TWalletInfo walletInfo);


    TServerNode getServerNodeByWalletInfoUid(Long uid);

    TServerNode getById(Long serverNodeId);
}
