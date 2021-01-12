package com.xinlian.admin.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.Base.BaseCriteria;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public class WalletTradeOrderService extends PageBaseService<TWalletTradeOrder> {

    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private TWalletInfoMapper walletInfoMapper;

    @Override
    public List<TWalletTradeOrder> query(TWalletTradeOrder walletTradeOrder){
        return walletTradeOrderMapper.query(walletTradeOrder);
    }

    public PageInfo queryRechargePage(Map<String, Object> searchParams) throws Exception {
        BaseCriteria criteria = BaseCriteria.newInstance();
        criteria.setPageParams(searchParams);
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        TWalletTradeOrder t = mapToModel(searchParams);
        return new PageInfo(walletTradeOrderMapper.queryRecharge(t));
    }

    @Transactional
    public Integer auditReject(Long tradeOrderId, String failReason) {
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setId(tradeOrderId);
        walletTradeOrder.setFailReason(failReason);
        walletTradeOrder.setOldTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.AUDIT_REJECT.getCode());
        int resultNum = walletTradeOrderMapper.updateWalletTradeOrder(walletTradeOrder);
        if(resultNum==1){
            //获取需要--解冻金额
            TWalletTradeOrder getTradeOrder = walletTradeOrderMapper.getByCriteria(walletTradeOrder);
            TWalletInfo unFreezeWalletInfo = new TWalletInfo();
            unFreezeWalletInfo.setUid(getTradeOrder.getUid());
            //交易表中金额是负值，所以这里转化为正值
            unFreezeWalletInfo.setFrozenNum(UdunBigDecimalUtil.convertPlus(getTradeOrder.getTradeCurrencyNum()));
            unFreezeWalletInfo.setCurrencyId(getTradeOrder.getCurrencyId());
            walletInfoMapper.disposalBalanceAndUnFreeze(unFreezeWalletInfo);
        }
        return resultNum;
    }

    public Integer auditPass(Long tradeOrderId) {
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setId(tradeOrderId);
        walletTradeOrder.setOldTradeStatus(WalletTradeOrderStatusEnum.APPLY.getCode());
        walletTradeOrder.setTradeStatus(WalletTradeOrderStatusEnum.ADMIN_PASS_PASS.getCode());
        return walletTradeOrderMapper.updateWalletTradeOrder(walletTradeOrder);
    }
}
