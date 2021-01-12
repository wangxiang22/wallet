package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.UserCurrencyStateReq;
import com.xinlian.common.request.SellCatReq;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Repository
public interface TWalletInfoMapper extends BaseMapper<TWalletInfo> {

    int insertBatch(List<TWalletInfo> list);

    int substactMoneyForOrem(TWalletInfo tWalletInfo);

    TWalletInfo getByCriteria(TWalletInfo walletInfo);

    TWalletInfo getByCriteriaNoLock(TWalletInfo walletInfo);

    int updateModel(TWalletInfo walletInfo);

    int disposalBalanceAndFreeze(TWalletInfo walletInfo);
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
     * 钱包解冻
     * @param walletInfo
     * @return
     */
    int disposalBalanceAndUnFreeze(TWalletInfo walletInfo);

    int allocationCurrencyAddress(TWalletInfo walletInfo);

    int despoit(UserCurrencyStateReq userCurrencyStateReq);

    int withdraw(UserCurrencyStateReq userCurrencyStateReq);

    int subFrozen(UserCurrencyStateReq userCurrencyStateReq);

    //增加链接人cat
    int batchChainOwnerAddCat(Map<String, Object> paramMap);

    Map<String,BigDecimal> queryTotalWalletData(@Param("currencyCode")String currencyCode);

    /**
     * 批量更新
     * @param list
     * @return
     */
    int batchUpdateWalletInfo(@Param("list") List<TWalletInfo> list);

    //火箭转入专用
    TWalletInfo queryWalletByUid(@Param("uid") Long uid,@Param("coinName") String coinName);
    //火箭使用
    Integer updateWalletMoney(BigDecimal balance, Long uid, String coinName);

    /**
     * 钱包余额扣除
     * @param amount 需扣除的金额
     * @param uid 用户id
     * @param currencyId 币种id
     * @return 修改结果
     */
    int updateReduceBalanceNum(@Param("amount") BigDecimal amount,@Param("uid") Long uid,@Param("currencyId") Long currencyId);

    /**
     * 钱包余额加钱
     * @param amount 需加到余额中的金额
     * @param uid 用户id
     * @param currencyId 币种id
     * @return 修改结果
     */
    int updateAddBalanceNum(@Param("amount") BigDecimal amount,@Param("uid") Long uid,@Param("currencyId") Long currencyId);

    /**
     * 只扣除冻结金额
     * @param walletInfo
     * @return
     */
    int updateFrozenNum(TWalletInfo walletInfo);

    /**
     * 智能合约冻结
     * @param sellCatReq
     * @return
     */
    int frozen(SellCatReq sellCatReq);

    List<TWalletInfo> queryAccount(Long sellerUid);

    Long queryUidbyAddr(String address);

    int updateModelByTrcRecharge(TWalletInfo updateWallet);


}
