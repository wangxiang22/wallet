package com.xinlian.member.server.controller.handler;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.NodeVoyageUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Song
 * @date 2020-08-05 15:30
 * @description 大航海节点交易路由规则
 */
@Component
public class JudgeSeaPatrolTradeRuleHandler {

    @Autowired
    private NodeVoyageUtil nodeVoyageUtil;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;

    public boolean checkBothIsSeaPatrol(WithdrawCurrencyRequest withdrawCurrencyRequest, TServerNode toServerNode){
        return this.doCheckBothIsSeaPatrol(withdrawCurrencyRequest.getServerNodeId(),
                toServerNode,
                Integer.parseInt(withdrawCurrencyRequest.getCoin_id()));
    }

    public void doCheckFormServerNode(WithdrawCurrencyRequest withdrawCurrencyRequest, TServerNode toServerNode){
        if(null==toServerNode){return;} //给转到公链
        Long fromServerNodeId = withdrawCurrencyRequest.getServerNodeId();
        Long toServerNodeId = toServerNode.getId();
        if( !nodeVoyageUtil.belongVoyageNode(fromServerNodeId) && nodeVoyageUtil.belongVoyageNode(toServerNodeId)){
            throw new BizException(ErrorInfoEnum.CHECK_IS_SEA_PATROL_SERVER);
        }
    }

    /**
     * check is Sea Patrol server node
     * @param fromServerNodeId 转自节点
     * @param toServerNode 转入节点
     * @param coinId 币种id
     */
    private boolean doCheckBothIsSeaPatrol(Long fromServerNodeId, TServerNode toServerNode, int coinId) {
        if(null==toServerNode){return true;}//给转到公链 -需要判断币种节点等之前开关
        Long toServerNodeId = toServerNode.getId();
        if( nodeVoyageUtil.belongVoyageNode(fromServerNodeId) && !nodeVoyageUtil.belongVoyageNode(toServerNodeId)){
            //目前只支持大航海计划同节点互转
            throw new BizException(ErrorInfoEnum.CHECK_IS_SEA_PATROL_SERVER);
            //产品说 先判断本节点情况，然后才是转出节点
            /**}else if( !nodeVoyageUtil.belongVoyageNode(fromServerNodeId) && nodeVoyageUtil.belongVoyageNode(toServerNodeId)){
             throw new BizException(ErrorInfoEnum.CHECK_IS_SEA_PATROL_SERVER);*/
        }else if( !nodeVoyageUtil.belongVoyageNode(fromServerNodeId) && !nodeVoyageUtil.belongVoyageNode(toServerNodeId)){
            return true;
        }else if(nodeVoyageUtil.belongVoyageNode(fromServerNodeId) && nodeVoyageUtil.belongVoyageNode(toServerNodeId)){
            //大航海两个节点，提示
            this.checkSeaPatrolByCurrency(coinId);
            return false;
        }
        //还有一种情况在走完 币种节点开关再走 (转自不是大航海，转入是大航海)-情况
        return true;
    }

    /**
     * 判断大航海节点币种是否支持 -写死支持USDT
     * @param coinId
     */
    private void checkSeaPatrolByCurrency(int coinId){
        //check is open time
        String openEndTime = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SEA_PATROL_TRADE_END_TIME.getBelongsSystemCode());
        String openStartTime = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SEA_PATROL_TRADE_START_TIME.getBelongsSystemCode());
        //参数未配置， 当前时间小于开始时间  当前时间大于截止时间
        if(StringUtils.isEmpty(openEndTime) || StringUtils.isEmpty(openStartTime)
                ||  System.currentTimeMillis() < Long.parseLong(openStartTime) || System.currentTimeMillis()> Long.parseLong(openEndTime)){
            throw new BizException("暂未开放!!");
        }
        //judge currencyid is != usdt return
        if(CurrencyEnum.USDT.getCurrencyId() != coinId){
            throw new BizException("暂不支持该币种!!");
        }
    }
}
