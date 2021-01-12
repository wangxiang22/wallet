package com.xinlian.admin.biz.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.xinlian.admin.biz.service.TemplateExportExcelService;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.response.EveryDayBillDetailRes;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lt
 * @date 2020/08/24
 **/
@Service
@Slf4j
public class TemplateExportExcelServiceImpl implements TemplateExportExcelService {
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;

    //  入金/进账的不同种des集合
    //内部充币
    private static final List<String> internalRechargeDesList = new ArrayList<>(Arrays.asList("充值","内部-转入","内部转账-转入","矿池支付余额-转入","空投发放","释放","释放CAT"));
    //外部充币
    private static final List<String> externalRechargeDesList = new ArrayList<>(Arrays.asList("充值到账","公链手动确认","外部充值","链上充值","链上充值-充值老地址找回"));
    //激活费用退还
    private static final List<String> activeReturnDesList = new ArrayList<>(Arrays.asList("矿机激活费用退款","退出激活返还"));
    //兑入得到
    private static final List<String> smartContractBuyIncomeDesList = new ArrayList<>(Collections.singletonList("买入CAT"));
    //兑出得到
    private static final List<String> smartContractSellIncomeDesList = new ArrayList<>(Collections.singletonList("卖出cat所得"));
    //活动奖励
    private static final List<String> activityRewardDesList = new ArrayList<>(Arrays.asList("CAG活动奖励转入","ceo购买","ceo购入cat","十大建设者奖励","年会一等奖","年会二等奖","年会三等奖","年会红包奖","抽奖所得","抽宝箱","活动所得","社区奖励","集福卡中奖"));
    //空投奖励
    private static final List<String> dropAwardDesList = new ArrayList<>(Arrays.asList("其他所得","拒绝回款","激活空投","空投所得"));
    //后台充值
    private static final List<String> backgroundRechargeDesList = new ArrayList<>(Arrays.asList("充值所得","后台充值","回购"));
    //火箭转入钱包
    private static final List<String> rocketTransferDesList = new ArrayList<>(Collections.singletonList("rocket转入"));
    //提现审核拒绝
    private static final List<String> withdrawAuditRefuseDesList = new ArrayList<>(Arrays.asList("Refusal to return","提现拒绝返回"));
    //兑出挂单超时
    private static final List<String> entryOrdersTimeoutDesList = new ArrayList<>(Collections.singletonList("挂单时间过期退还"));
    //出售商品
    private static final List<String> sellGoodsDesList = new ArrayList<>(Collections.singletonList("出售商品"));
    //内部提币
    private static final List<String> internalCashDesList = new ArrayList<>(Arrays.asList("内部转账","内部转账-转出","提现-内部转账","系统协助提币","转出","转账"));
    //外部提币
    private static final List<String> externalCashDesList = new ArrayList<>(Arrays.asList("提币","提现","提现成功","绑定交易所扣费"));
    //激活算能支付
    private static final List<String> activePayDesList = new ArrayList<>(Collections.singletonList("激活矿机"));
    //算能质押
    private static final List<String> powerPledgeDesList = new ArrayList<>(Arrays.asList("保证金交纳","算能设备兑换"));
    //兑出失去CAT
    private static final List<String> smartContractSellDeductDesList = new ArrayList<>(Collections.singletonList("cat挂单冻结"));
    //兑入失去USDT
    private static final List<String> smartContractBuyDeductDesList = new ArrayList<>(Collections.singletonList("买入CAT"));
    //手续费CAG
    private static final List<String> smartContractBuyFeeDesList = new ArrayList<>(Collections.singletonList("兑入CAT手续费"));
    //钱包转出到火箭ex
    private static final List<String> transferOutRocketDesList = new ArrayList<>(Arrays.asList("多充扣款","多转扣款","转出到rocket"));
    //购买商品
    private static final List<String> buyGoodsDesList = new ArrayList<>(Collections.singletonList("购买商品"));
    //外部提币冻结（小于0）
    private static final List<String> externalCashFreezeDesList = new ArrayList<>(Collections.singletonList("提现冻结"));
    //算能质押冻结
    private static final List<String> powerPledgeFreezeDesList = new ArrayList<>(Collections.singletonList("保证金交纳"));
    //兑出冻结（单独sql执行，status状态是7，type是2和3，与其他冻结不统一）
    private static final List<String> smartContractSellFreezeDesList = new ArrayList<>(Collections.singletonList("cat挂单冻结"));
    //挂单手续费-CAG冻结（单独sql执行，status状态是7，type是2，与其他冻结不统一）
    private static final List<String> smartContractSellFeeDesList = new ArrayList<>(Collections.singletonList("挂单手续费-CAG冻结"));


    @Override
    public void exportEveryDayBillDetail(HttpServletResponse response, String reqTime) throws IOException {
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(reqTime);
            String fileNameTime = new SimpleDateFormat("MMdd").format(dateTime);
            String templateFileName =
                    ResourceUtils.getURL("classpath:").getPath() + File.separator + "CATWallet每日资产统计数据模板.xlsx";
            log.info(templateFileName + "=================================");
            //根据对象填充
            String fileName = URLEncoder.encode("CATWallet每日资产统计数据_" + fileNameTime,"utf-8");
            //自动填充到第一个sheet，然后文件流会自动关闭
            //实体类，然后查询数据并set
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(reqTime);
            String time = new SimpleDateFormat("yyyy年MM月dd日").format(date);
            EveryDayBillDetailRes everyDayBillDetailRes = this.createEveryDayBillDetailRes(time,reqTime);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition","attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).sheet().doFill(everyDayBillDetailRes);
        } catch (Exception e) {
            //重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
//            Map<String,String> map = new HashMap<>();
//            map.put("status","failure");
//            map.put("massage","下载文件失败" + e.getMessage());
//            response.getWriter().println(JSON.toJSONString(map));
            log.error("=========下载文件失败==========={}:",e.toString(),e);
            throw new BizException("下载文件失败");
        }
    }

    private EveryDayBillDetailRes createEveryDayBillDetailRes(String time,String reqTime) {
        Long usdtCurrencyId = Long.parseLong(CurrencyEnum.USDT.getCurrencyId() + "");
        Long catCurrencyId = Long.parseLong(CurrencyEnum.CAT.getCurrencyId() + "");
        Long cagCurrencyId = Long.parseLong(CurrencyEnum.CAG.getCurrencyId() + "");
        EveryDayBillDetailRes everyDayBillDetailRes = new EveryDayBillDetailRes();
        //导出的数据日期
        everyDayBillDetailRes.setTime(time);
        //内部充币
        everyDayBillDetailRes.setUsdtInternalRecharge(this.getDifferentTakeInAmount(internalRechargeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatInternalRecharge(this.getDifferentTakeInAmount(internalRechargeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagInternalRecharge(this.getDifferentTakeInAmount(internalRechargeDesList,reqTime, cagCurrencyId));
        //外部充币
        everyDayBillDetailRes.setUsdtExternalRecharge(this.getDifferentTakeInAmount(externalRechargeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatExternalRecharge(this.getDifferentTakeInAmount(externalRechargeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagExternalRecharge(this.getDifferentTakeInAmount(externalRechargeDesList,reqTime, cagCurrencyId));
        //激活费用退还
        everyDayBillDetailRes.setUsdtActiveReturn(this.getDifferentTakeInAmount(activeReturnDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatActiveReturn(this.getDifferentTakeInAmount(activeReturnDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagActiveReturn(this.getDifferentTakeInAmount(activeReturnDesList,reqTime, cagCurrencyId));
        //兑入得到
        everyDayBillDetailRes.setUsdtSmartContractBuyIncome(this.getDifferentTakeInAmount(smartContractBuyIncomeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractBuyIncome(this.getDifferentTakeInAmount(smartContractBuyIncomeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractBuyIncome(this.getDifferentTakeInAmount(smartContractBuyIncomeDesList,reqTime, cagCurrencyId));
        //兑出得到
        everyDayBillDetailRes.setUsdtSmartContractSellIncome(this.getDifferentTakeInAmount(smartContractSellIncomeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractSellIncome(this.getDifferentTakeInAmount(smartContractSellIncomeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractSellIncome(this.getDifferentTakeInAmount(smartContractSellIncomeDesList,reqTime, cagCurrencyId));
        //活动奖励
        everyDayBillDetailRes.setUsdtActivityReward(this.getDifferentTakeInAmount(activityRewardDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatActivityReward(this.getDifferentTakeInAmount(activityRewardDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagActivityReward(this.getDifferentTakeInAmount(activityRewardDesList,reqTime, cagCurrencyId));
        //空投奖励
        everyDayBillDetailRes.setUsdtDropAward(this.getDifferentTakeInAmount(dropAwardDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatDropAward(this.getDifferentTakeInAmount(dropAwardDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagDropAward(this.getDifferentTakeInAmount(dropAwardDesList,reqTime, cagCurrencyId));
        //后台充值
        everyDayBillDetailRes.setUsdtBackgroundRecharge(this.getDifferentTakeInAmount(backgroundRechargeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatBackgroundRecharge(this.getDifferentTakeInAmount(backgroundRechargeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagBackgroundRecharge(this.getDifferentTakeInAmount(backgroundRechargeDesList,reqTime, cagCurrencyId));
        //火箭转入钱包
        everyDayBillDetailRes.setUsdtRocketTransfer(this.getDifferentTakeInAmount(rocketTransferDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatRocketTransfer(this.getDifferentTakeInAmount(rocketTransferDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagRocketTransfer(this.getDifferentTakeInAmount(rocketTransferDesList,reqTime, cagCurrencyId));
        //提现审核拒绝
        everyDayBillDetailRes.setUsdtWithdrawAuditRefuse(this.getDifferentTakeInAmount(withdrawAuditRefuseDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatWithdrawAuditRefuse(this.getDifferentTakeInAmount(withdrawAuditRefuseDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagWithdrawAuditRefuse(this.getDifferentTakeInAmount(withdrawAuditRefuseDesList,reqTime, cagCurrencyId));
        //兑出挂单超时
        everyDayBillDetailRes.setUsdtEntryOrdersTimeout(this.getDifferentTakeInAmount(entryOrdersTimeoutDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatEntryOrdersTimeout(this.getDifferentTakeInAmount(entryOrdersTimeoutDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagEntryOrdersTimeout(this.getDifferentTakeInAmount(entryOrdersTimeoutDesList,reqTime, cagCurrencyId));
        //出售商品
        everyDayBillDetailRes.setUsdtSellGoods(this.getDifferentTakeInAmount(sellGoodsDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSellGoods(this.getDifferentTakeInAmount(sellGoodsDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSellGoods(this.getDifferentTakeInAmount(sellGoodsDesList,reqTime, cagCurrencyId));
        //内部提币
        everyDayBillDetailRes.setUsdtInternalCash(this.getDifferentExpenditureAmount(internalCashDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatInternalCash(this.getDifferentExpenditureAmount(internalCashDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagInternalCash(this.getDifferentExpenditureAmount(internalCashDesList,reqTime, cagCurrencyId));
        //外部提币
        everyDayBillDetailRes.setUsdtExternalCash(this.getDifferentExpenditureAmount(externalCashDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatExternalCash(this.getDifferentExpenditureAmount(externalCashDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagExternalCash(this.getDifferentExpenditureAmount(externalCashDesList,reqTime, cagCurrencyId));
        //激活算能支付
        everyDayBillDetailRes.setUsdtActivePay(this.getDifferentExpenditureAmount(activePayDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatActivePay(this.getDifferentExpenditureAmount(activePayDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagActivePay(this.getDifferentExpenditureAmount(activePayDesList,reqTime, cagCurrencyId));
        //算能质押
        everyDayBillDetailRes.setUsdtPowerPledge(this.getDifferentExpenditureAmount(powerPledgeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatPowerPledge(this.getDifferentExpenditureAmount(powerPledgeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagPowerPledge(this.getDifferentExpenditureAmount(powerPledgeDesList,reqTime, cagCurrencyId));
        //兑出失去CAT
        everyDayBillDetailRes.setUsdtSmartContractSellDeduct(this.getDifferentExpenditureAmount(smartContractSellDeductDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractSellDeduct(this.getDifferentExpenditureAmount(smartContractSellDeductDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractSellDeduct(this.getDifferentExpenditureAmount(smartContractSellDeductDesList,reqTime, cagCurrencyId));
        //兑入失去USDT
        everyDayBillDetailRes.setUsdtSmartContractBuyDeduct(this.getDifferentExpenditureAmount(smartContractBuyDeductDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractBuyDeduct(this.getDifferentExpenditureAmount(smartContractBuyDeductDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractBuyDeduct(this.getDifferentExpenditureAmount(smartContractBuyDeductDesList,reqTime, cagCurrencyId));
        //手续费CAG
        everyDayBillDetailRes.setUsdtSmartContractBuyFee(this.getDifferentExpenditureAmount(smartContractBuyFeeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractBuyFee(this.getDifferentExpenditureAmount(smartContractBuyFeeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractBuyFee(this.getDifferentExpenditureAmount(smartContractBuyFeeDesList,reqTime, cagCurrencyId));
        //钱包转出到火箭ex
        everyDayBillDetailRes.setUsdtTransferOutRocket(this.getDifferentExpenditureAmount(transferOutRocketDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatTransferOutRocket(this.getDifferentExpenditureAmount(transferOutRocketDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagTransferOutRocket(this.getDifferentExpenditureAmount(transferOutRocketDesList,reqTime, cagCurrencyId));
        //购买商品
        everyDayBillDetailRes.setUsdtBuyGoods(this.getDifferentExpenditureAmount(buyGoodsDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatBuyGoods(this.getDifferentExpenditureAmount(buyGoodsDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagBuyGoods(this.getDifferentExpenditureAmount(buyGoodsDesList,reqTime, cagCurrencyId));
        //外部提币冻结
        everyDayBillDetailRes.setUsdtExternalCashFreeze(this.getDifferentFreezeAmountByStatusOne(externalCashFreezeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatExternalCashFreeze(this.getDifferentFreezeAmountByStatusOne(externalCashFreezeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagExternalCashFreeze(this.getDifferentFreezeAmountByStatusOne(externalCashFreezeDesList,reqTime, cagCurrencyId));
        //算能质押冻结
        everyDayBillDetailRes.setUsdtPowerPledgeFreeze(this.getDifferentFreezeAmountByStatusOne(powerPledgeFreezeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatPowerPledgeFreeze(this.getDifferentFreezeAmountByStatusOne(powerPledgeFreezeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagPowerPledgeFreeze(this.getDifferentFreezeAmountByStatusOne(powerPledgeFreezeDesList,reqTime, cagCurrencyId));
        //兑出冻结（单独sql执行，status状态是7，type是2和3，与其他冻结不统一）
        everyDayBillDetailRes.setUsdtSmartContractSellFreeze(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFreezeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractSellFreeze(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFreezeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractSellFreeze(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFreezeDesList,reqTime, cagCurrencyId));
        //挂单手续费-CAG冻结（单独sql执行，status状态是7，type是2，与其他冻结不统一）
        everyDayBillDetailRes.setUsdtSmartContractSellFee(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFeeDesList,reqTime, usdtCurrencyId));
        everyDayBillDetailRes.setCatSmartContractSellFee(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFeeDesList,reqTime, catCurrencyId));
        everyDayBillDetailRes.setCagSmartContractSellFee(this.getDifferentFreezeAmountByStatusSeven(smartContractSellFeeDesList,reqTime, cagCurrencyId));
        return everyDayBillDetailRes;
    }

    /**
     * 入金不同des集合通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    private BigDecimal getDifferentTakeInAmount(List<String> desList, String reqTime ,Long currencyId) {
        BigDecimal differentTakeInAmount = walletTradeOrderMapper.findDifferentTakeInAmount(desList, reqTime, currencyId);
        differentTakeInAmount = UdunBigDecimalUtil.checkBigDecimalEightDigit(differentTakeInAmount);
        return differentTakeInAmount;
    }

    /**
     * 出金不同des集合通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    private BigDecimal getDifferentExpenditureAmount(List<String> desList, String reqTime ,Long currencyId) {
        BigDecimal differentExpenditureAmount = walletTradeOrderMapper.findDifferentExpenditureAmount(desList, reqTime, currencyId);
        differentExpenditureAmount = UdunBigDecimalUtil.checkBigDecimalEightDigit(differentExpenditureAmount);
        return differentExpenditureAmount;
    }

    /**
     * 冻结金额不同des集合且status为1的通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    private BigDecimal getDifferentFreezeAmountByStatusOne(List<String> desList, String reqTime ,Long currencyId) {
        BigDecimal amount = walletTradeOrderMapper.findDifferentFreezeAmountByStatusOne(desList, reqTime, currencyId);
        amount = UdunBigDecimalUtil.checkBigDecimalEightDigit(amount);
        return amount;
    }

    /**
     * 冻结金额不同des集合且status为7的通用统计语句
     * @param currencyId 币种id
     * @param reqTime 参数时间
     * @param desList des集合
     * @return 统计金额
     */
    private BigDecimal getDifferentFreezeAmountByStatusSeven(List<String> desList, String reqTime ,Long currencyId) {
        BigDecimal amount = walletTradeOrderMapper.findDifferentFreezeAmountByStatusSeven(desList, reqTime, currencyId);
        amount = UdunBigDecimalUtil.checkBigDecimalEightDigit(amount);
        return amount;
    }
}
