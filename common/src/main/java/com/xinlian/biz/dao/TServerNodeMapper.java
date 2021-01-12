package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TServerNode;
import com.xinlian.biz.model.TWalletInfo;
import org.springframework.stereotype.Component;


@Component
public interface TServerNodeMapper extends BaseMapper<TServerNode> {

    /**
     * 根据客户转出地址关系对应的serverNodeId来找到对应的节点信息
     * @param walletInfo
     * @return
     */
    TServerNode getServerNodeByWithdrawAddress(TWalletInfo walletInfo);

    /**
     * 根据客户钱包uid-serverNode-找到对应的节点信息
     * @param uId
     * @return
     */
    TServerNode getServerNodeByWalletInfoUid(Long uId);
}
