package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.member.biz.service.TWalletInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 客户钱包表 服务实现类
 * </p>
 *
 * @author wjf
 * @since 2019-12-23
 */
@Service
public class TWalletInfoServiceImpl implements TWalletInfoService {

    @Autowired
    private TWalletInfoMapper walletInfoMapper;

    @Override
    public TWalletInfo getByCriteria(TWalletInfo walletInfo){
        return walletInfoMapper.getByCriteria(walletInfo);
    }

    @Override
    public TWalletInfo getByCriteriaNoLock(TWalletInfo walletInfo){
        return walletInfoMapper.getByCriteriaNoLock(walletInfo);
    }

    @Override
    public int updateModel(TWalletInfo walletInfo){
        return walletInfoMapper.updateModel(walletInfo);
    }

    /**
     * 转入资产 - 往转入账户增加资产
     * @param toWallInfo
     * @return
     */
    @Override
    public int toWalletInfoAddBalanceNum(TWalletInfo toWallInfo){
        return walletInfoMapper.toWalletInfoAddBalanceNum(toWallInfo);
    }

    /**
     * 转出资产 - 往转出账户减资产
     * @param toWallInfo
     * @return
     */
    @Override
    public int fromWalletInfoAbatmentBalanceNum(TWalletInfo toWallInfo){
        return walletInfoMapper.fromWalletInfoAbatmentBalanceNum(toWallInfo);
    }

    @Override
    public int disposalBalanceAndFreeze(Long userId, Long coin_id, BigDecimal num) {
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setUid(userId);
        walletInfo.setCurrencyId(coin_id);
        walletInfo.setFrozenNum(num);
        return walletInfoMapper.disposalBalanceAndFreeze(walletInfo);
    }

    @Override
    public int disposalBalanceAndUnFreeze(Long userId, Long coin_id, BigDecimal num) {
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setUid(userId);
        walletInfo.setCurrencyId(coin_id);
        walletInfo.setFrozenNum(num);
        return walletInfoMapper.disposalBalanceAndUnFreeze(walletInfo);
    }

    @Override
    public int allocationCurrencyAddress(Long userId,String address){
        TWalletInfo walletInfo = new TWalletInfo();
        walletInfo.setUid(userId);
        walletInfo.setCurrencyAddress(address);
        return walletInfoMapper.allocationCurrencyAddress(walletInfo);
    }
}
