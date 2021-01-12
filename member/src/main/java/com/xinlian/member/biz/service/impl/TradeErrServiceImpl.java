package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.model.UserCurrencyStateReq;
import com.xinlian.common.enums.WalletTradeTypeEnum;
import com.xinlian.member.biz.service.TradeErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
@Service
public class TradeErrServiceImpl implements TradeErrService {
    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public void saveErrorLog(UserCurrencyStateReq userCurrencyStateReq) {

        TWalletTradeOrder tWalletTradeOrder = new TWalletTradeOrder();
        tWalletTradeOrder.setUid(userCurrencyStateReq.getUid());
        tWalletTradeOrder.setTradeCurrencyNum(userCurrencyStateReq.getType()==1?userCurrencyStateReq.getAmount().abs():userCurrencyStateReq.getAmount().abs().multiply(new BigDecimal(-1)));
        tWalletTradeOrder.setCurrencyId(userCurrencyStateReq.getCurrencyId());
        tWalletTradeOrder.setCurrencyCode(userCurrencyStateReq.getCoinName().toUpperCase());
        tWalletTradeOrder.setCreateTime(new Date());
        tWalletTradeOrder.setTradeStatus(6);//交易失败
        tWalletTradeOrder.setTradeType(userCurrencyStateReq.getType());
        tWalletTradeOrder.setDes(userCurrencyStateReq.getType()==1? WalletTradeTypeEnum.FROM_ROCKET.getTradeDesc():WalletTradeTypeEnum.TO_ROCKET.getTradeDesc());
        tWalletTradeOrder.setIsin(1);
        tWalletTradeOrderMapper.exchangeWalletTrade(tWalletTradeOrder);
    }
}
