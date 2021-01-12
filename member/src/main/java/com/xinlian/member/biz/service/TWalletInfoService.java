package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TWalletInfo;

import java.math.BigDecimal;

/**
 * <p>
 * 客户钱包表 服务类
 * </p>
 *
 * @author wjf
 * @since 2019-12-23
 */
public interface TWalletInfoService {


    /**
     * 根据对象参数获取符合的一条记录
     * @param walletInfo
     * @return
     */
    TWalletInfo getByCriteria(TWalletInfo walletInfo);

    TWalletInfo getByCriteriaNoLock(TWalletInfo walletInfo);

    int updateModel(TWalletInfo walletInfo);

    /**
     * 转入资产 - 往转入账户增加资产
     * @param toWallInfo
     * @return
     */
    int toWalletInfoAddBalanceNum(TWalletInfo toWallInfo);

    /**
     * 转出资产 - 往转出账户减资产
     * @param toWallInfo
     * @return
     */
    int fromWalletInfoAbatmentBalanceNum(TWalletInfo toWallInfo);

    /**
     * 处理钱包余额金额与冻结金额
     * @param userId
     * @param coin_id
     * @param num
     * @return
     */
    int disposalBalanceAndFreeze(Long userId, Long coin_id, BigDecimal num);

    /**
     * 解冻钱包冻结金额
     * @param userId
     * @param coin_id
     * @param num
     * @return
     */
    int disposalBalanceAndUnFreeze(Long userId, Long coin_id, BigDecimal num);

    int allocationCurrencyAddress(Long userId, String address);
}
