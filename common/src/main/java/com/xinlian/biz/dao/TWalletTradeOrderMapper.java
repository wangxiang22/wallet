package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.biz.model.WalletTradeCount;
import com.xinlian.common.dto.PledgeManagePageDto;
import com.xinlian.common.request.CurrencyInfoRes;
import com.xinlian.common.request.GetOneTradeInfoReq;
import com.xinlian.common.request.PledgeManagePageReq;
import com.xinlian.common.request.TradeInfoReq;
import com.xinlian.common.response.CurrencyBalanceRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Repository
public interface TWalletTradeOrderMapper extends BaseMapper<TWalletTradeOrder> {

    /**
     * 更新
     * @param walletTradeOrder
     * @return
     */
    Integer updateWalletTradeOrder(TWalletTradeOrder walletTradeOrder);


    TWalletTradeOrder getByCriteria(TWalletTradeOrder whereModel);

    List<TWalletTradeOrder> getTradeInfo(TradeInfoReq tradeInfoReq);

    TWalletTradeOrder getOneTradeInfo(GetOneTradeInfoReq getOneTradeInfoReq);

    CurrencyBalanceRes getCurrencyBalanceInfo(CurrencyInfoRes currencyInfoRes);

    TWalletTradeOrder getAdminAuditPassTradeOrder();

    List<TWalletTradeOrder> queryRecharge(TWalletTradeOrder walletTradeOrder);

    List<TWalletTradeOrder> query(TWalletTradeOrder walletTradeOrder);

    WalletTradeCount queryToday(Long uid);

    WalletTradeCount queryTodayWithdrawAmount(Long uid);

    WalletTradeCount queryTodayDespositAmount(Long uid);
    //释放链权人cat-添加对应交易记录
    int batchChainOwnerTradeRecord(List<TWalletTradeOrder> list);

    int exchangeWalletTrade(TWalletTradeOrder tWalletTradeOrder);

    /**
     * 获取当天币种交易的总数
     * @param currencyCode 币种code
     * @return
     */
    BigDecimal getTodayWalletData(@Param(value = "currencyCode") String currencyCode);

    /**
     * 保存交易订单信息
     * @param walletTradeOrder
     * @return
     */
    Integer saveModel(TWalletTradeOrder walletTradeOrder);

    /**
     * 分页查询算能质押申请信息
     * @param pledgeManagePageReq
     * @return
     */
    List<PledgeManagePageDto> findPledgePage(PledgeManagePageReq pledgeManagePageReq);

    /**
     * 更新审核状态及备注、冻结理由
     * @param walletTradeOrder
     * @return
     */
    int updatePledgeTradeOrder(TWalletTradeOrder walletTradeOrder);

    /**
     * 根据 trade_status trade_type des 获取Uids集合
     * @param walletTradeOrder
     * @return
     */
    List<TWalletTradeOrder> getUidsByIdxQyTradeLog(TWalletTradeOrder walletTradeOrder);

    List<TWalletTradeOrder> getUidsByIdxQyTradeLogAndUids(Map<String,Object> paramsMap);

    /**
     * 进账金额
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 进账金额
     */
    BigDecimal findTakeInAmount(Long currencyId, String startTime, String endTime);

    /**
     * 出账金额
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 出账金额
     */
    BigDecimal findExpenditureAmount(Long currencyId, String startTime, String endTime);

    //币种是CAT或CAG的情况，出账金额需要扣除status=6、type=1、des="挂单时间过期退还"的金额（负值的出账金额+需要扣除的正值的金额）
    BigDecimal findNeedDeductAmount(Long currencyId, String startTime, String endTime);

    /**
     * 根据条件查询交易总进账金额
     * @param tradeStatus 交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
     * @param tradeType 交易类型 (充值：1；提币：2;兑换：3  )
     * @param des 描述
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易总金额
     */
    BigDecimal findTradeTotalInAmount(Integer tradeStatus, Integer tradeType, String des, Long currencyId, String startTime, String endTime);

    /**
     * 根据条件查询交易总出账金额
     * @param tradeStatus 交易状态 申请-1;后台审核通过-2;等待回调-3;审核驳回-4;审核通过-5;交易失败-6;交易成功-7
     * @param tradeType 交易类型 (充值：1；提币：2;兑换：3  )
     * @param des 描述
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 交易总金额
     */
    BigDecimal findTradeTotalOutAmount(Integer tradeStatus, Integer tradeType, String des, Long currencyId, String startTime, String endTime);

    /**
     * 查找质押人数（包含申请、拒绝、通过三个状态）
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 质押人数
     */
    Long findPledgeMiningPopulation(Long currencyId, String startTime, String endTime);

    /**
     * 查找质押金额（包含申请、拒绝、通过三个状态）
     * @param currencyId 币种id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 质押金额
     */
    BigDecimal findPledgeMiningAmount(Long currencyId, String startTime, String endTime);

    /**
     * 入金不同des集合通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    BigDecimal findDifferentTakeInAmount(@Param(value = "list") List<String> desList, String reqTime ,Long currencyId);

    /**
     * 出金不同des集合通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    BigDecimal findDifferentExpenditureAmount(@Param(value = "list") List<String> desList, String reqTime ,Long currencyId);

    /**
     * 冻结金额不同des集合且status为1的通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    BigDecimal findDifferentFreezeAmountByStatusOne(@Param(value = "list") List<String> desList, String reqTime ,Long currencyId);

    /**
     * 冻结金额不同des集合且status为7的通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    BigDecimal findDifferentFreezeAmountByStatusSeven(@Param(value = "list") List<String> desList, String reqTime ,Long currencyId);
}


