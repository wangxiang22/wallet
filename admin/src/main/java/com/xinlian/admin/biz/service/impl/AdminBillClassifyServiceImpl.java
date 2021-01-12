package com.xinlian.admin.biz.service.impl;

import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.admin.biz.service.AdminBillClassifyService;
import com.xinlian.biz.dao.AdminBalanceBillOperationMapper;
import com.xinlian.biz.dao.AdminBillClassifyMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.AdminBillClassify;
import com.xinlian.biz.utils.CommonRedisClient;
import com.xinlian.common.enums.BillClassifyDesEnum;
import com.xinlian.common.enums.BillClassifyEnum;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.common.request.BillAuditReq;
import com.xinlian.common.request.BillClassifyShowHideReq;
import com.xinlian.common.response.BillAuditRes;
import com.xinlian.common.response.BillDetailRes;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 账单分类表 服务实现类
 * </p>
 *
 * @author lt
 * @since 2020-07-30
 */
@Service
public class AdminBillClassifyServiceImpl implements AdminBillClassifyService {

    @Autowired
    private AdminBillClassifyMapper adminBillClassifyMapper;
    @Autowired
    private CommonRedisClient commonRedisClient;
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;
    @Autowired
    private AdminBalanceBillOperationMapper adminBalanceBillOperationMapper;

    @Override
    public ResponseResult<List<AdminBillClassify>> findAllBillClassify() {
        return ResponseResult.<List<AdminBillClassify>>builder().code(ErrorInfoEnum.SUCCESS.getCode()).result(this.findAll()).build();
    }

    @Override
    public ResponseResult<List<AdminBillClassify>> findNotShowBillClassify() {
        List<AdminBillClassify> billClassifyList = this.findAll();
        //筛选出未展示的账单分类列表
        List<AdminBillClassify> notShowBillClassifyList = new ArrayList<>();
        billClassifyList.stream().filter(e->e.getShowState() == 0).forEach(notShowBillClassifyList::add);
        return ResponseResult.<List<AdminBillClassify>>builder().code(ErrorInfoEnum.SUCCESS.getCode()).result(notShowBillClassifyList).build();
    }

    @Override
    public ResponseResult updateShowBillClassify(BillClassifyShowHideReq req) {
        int resultCount = adminBillClassifyMapper.updateShowHide(req);
        if (resultCount == 0) {
            return ResponseResult.error("修改账单分类失败");
        }
        commonRedisClient.deleteByKey(RedisConstant.ADMIN_BILL_CLASSIFY);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult findTotalBill(BillAuditReq req) {
        int currencyIdInt = CurrencyEnum.getCurrencyIdByCurrencyCode(req.getCurrencyName());
        if(currencyIdInt==0) {
            throw new BizException("找不到对应currencyName:【"+req.getCurrencyName()+"】");
        }
        Long currencyId = Long.parseLong(currencyIdInt + "");
        //1.进账金额
        BigDecimal takeInAmount = walletTradeOrderMapper.findTakeInAmount(currencyId, req.getStartTime(), req.getEndTime());
        takeInAmount = UdunBigDecimalUtil.checkBigDecimal(takeInAmount);
        //2.平账进账金额
        BigDecimal balanceBillTakeInAmount = adminBalanceBillOperationMapper.findBalanceBillTakeInAmount(currencyId, req.getStartTime(), req.getEndTime());
        balanceBillTakeInAmount = UdunBigDecimalUtil.checkBigDecimal(balanceBillTakeInAmount);
        //3.总进账金额
        BigDecimal totalTakeInAmount = takeInAmount.add(balanceBillTakeInAmount).setScale(4, BigDecimal.ROUND_DOWN);
        //4.出账金额(绝对值)
        BigDecimal expenditureAmount = walletTradeOrderMapper.findExpenditureAmount(currencyId, req.getStartTime(), req.getEndTime());
        expenditureAmount = UdunBigDecimalUtil.checkAbsBigDecimal(expenditureAmount);
        //4-1.币种是CAT或CAG的情况，出账金额需要扣除"挂单时间过期退还"的金额（绝对值的出账金额-需要扣除的正值的金额）
        if (currencyId == CurrencyEnum.CAT.getCurrencyId() || currencyId == CurrencyEnum.CAG.getCurrencyId()) {
            BigDecimal returnExpenditureAmount = walletTradeOrderMapper.findNeedDeductAmount(currencyId, req.getStartTime(), req.getEndTime());
            returnExpenditureAmount = UdunBigDecimalUtil.checkAbsBigDecimal(returnExpenditureAmount);
            expenditureAmount = expenditureAmount.subtract(returnExpenditureAmount).setScale(4, BigDecimal.ROUND_DOWN);
        }
        //5.平账出账金额(绝对值)
        BigDecimal balanceBillExpenditureAmount = adminBalanceBillOperationMapper.findBalanceBillExpenditureAmount(currencyId, req.getStartTime(), req.getEndTime());
        balanceBillExpenditureAmount = UdunBigDecimalUtil.checkAbsBigDecimal(balanceBillExpenditureAmount);
        //6.总出账金额(绝对值)
        BigDecimal totalExpenditureAmount = expenditureAmount.add(balanceBillExpenditureAmount).setScale(4, BigDecimal.ROUND_DOWN);
        //7.差额(绝对值相减,总进账金额-总出账金额(绝对值))
        BigDecimal differenceAmount = totalTakeInAmount.subtract(totalExpenditureAmount).setScale(4, BigDecimal.ROUND_DOWN);
        //8.质押人数（包含申请、拒绝、通过三个状态）
        Long pledgeMiningPopulation = walletTradeOrderMapper.findPledgeMiningPopulation(currencyId, req.getStartTime(), req.getEndTime());
        //9.质押金额（包含申请、拒绝、通过三个状态，负数）
        BigDecimal pledgeMiningAmount = walletTradeOrderMapper.findPledgeMiningAmount(currencyId, req.getStartTime(), req.getEndTime());
        pledgeMiningAmount = UdunBigDecimalUtil.checkBigDecimal(pledgeMiningAmount);
        //返回值
        return ResponseResult.ok(this.createBillAuditRes(takeInAmount,balanceBillTakeInAmount,totalTakeInAmount,
                expenditureAmount,balanceBillExpenditureAmount,totalExpenditureAmount,differenceAmount,pledgeMiningPopulation,pledgeMiningAmount));
    }

    @Override
    public ResponseResult findBillDetailList(BillAuditReq req) {
        int currencyIdInt = CurrencyEnum.getCurrencyIdByCurrencyCode(req.getCurrencyName());
        if(currencyIdInt==0) {
            throw new BizException("找不到对应currencyName:【"+req.getCurrencyName()+"】");
        }
        Long currencyId = Long.parseLong(currencyIdInt + "");
        //1.查看所有展示的账单分类列表
        List<AdminBillClassify> showBillClassifyList = this.findShowBillClassify();
        if (showBillClassifyList.size() == 0) {
            return ResponseResult.ok(new ArrayList<>());
        }
        //2.所有展示的账单分类的对应账单数据
        List<BillDetailRes> billDetailResList = new ArrayList<>();
        for (AdminBillClassify adminBillClassify : showBillClassifyList) {
            if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.INTERNAL_TRANSFER.getBillClassifyName())){
                //内部转账
                this.getInternalTransfer(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.CHAIN_RECHARGE.getBillClassifyName())){
                //链上充值
                this.getChainRecharge(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.WITHDRAWAL_COIN.getBillClassifyName())){
                //提币
                this.getWithdrawalCoin(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.ROCKET_MUTUAL_TRANSFER.getBillClassifyName())){
                //rocket互转
                this.getRocketMutualTransfer(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.PLEDGE_MINING.getBillClassifyName())){
                //质押挖矿
                this.getPledgeMining(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.SMART_CONTRACTS.getBillClassifyName())){
                //智能合约：进账：卖出cat所得【USDT】+买入cat【CAT】+挂单时间过期退还【CAT、CAG】，
                //         出账(绝对值)：cat挂单冻结【CAT】+买入cat【USDT】+兑入cat手续费【CAG】+挂单手续费-CAG冻结【CAG】
                this.getSmartContracts(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.ACTIVE_MINING.getBillClassifyName())) {
                //激活矿机
                this.getActiveMining(currencyId,req,billDetailResList);
            }else if (adminBillClassify.getBillName().equalsIgnoreCase(BillClassifyEnum.GOODS_SELL_BUY.getBillClassifyName())) {
                //商品出售购买
                this.getGoodsSellBuy(currencyId,req,billDetailResList);
            }
        }
        return ResponseResult.ok(billDetailResList);
    }

    /**
     * 内部转账模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getInternalTransfer(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //内部转账：进账：内部转账-转入，出账(绝对值)：内部转账-转出
        BigDecimal internalTransferInAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.INTERNAL_TRANSFER_IN.getTradeStatus(), BillClassifyDesEnum.INTERNAL_TRANSFER_IN.getTradeType(),
                BillClassifyDesEnum.INTERNAL_TRANSFER_IN.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        internalTransferInAmount = UdunBigDecimalUtil.checkBigDecimal(internalTransferInAmount);
        BigDecimal internalTransferOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.INTERNAL_TRANSFER_OUT.getTradeStatus(), BillClassifyDesEnum.INTERNAL_TRANSFER_OUT.getTradeType(),
                BillClassifyDesEnum.INTERNAL_TRANSFER_OUT.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        internalTransferOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(internalTransferOutAmount);
        BillDetailRes billDetailRes = this.createBillDetailRes(Long.parseLong(BillClassifyEnum.INTERNAL_TRANSFER.getClassifyId() + ""),BillClassifyEnum.INTERNAL_TRANSFER.getBillClassifyName(), internalTransferInAmount, internalTransferOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 链上充值模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getChainRecharge(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //链上充值：进账：链上充值，出账(绝对值)：无
        BigDecimal chainRechargeAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.CHAIN_RECHARGE.getTradeStatus(), BillClassifyDesEnum.CHAIN_RECHARGE.getTradeType(),
                BillClassifyDesEnum.CHAIN_RECHARGE.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        chainRechargeAmount = UdunBigDecimalUtil.checkBigDecimal(chainRechargeAmount);
        BigDecimal chainRechargeOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(null);
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.CHAIN_RECHARGE.getClassifyId() + ""),BillClassifyEnum.CHAIN_RECHARGE.getBillClassifyName(), chainRechargeAmount, chainRechargeOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 提币模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getWithdrawalCoin(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //提币：进账：无，出账(绝对值)：提币
        BigDecimal withdrawalCoinInAmount = UdunBigDecimalUtil.checkBigDecimal(null);
        BigDecimal withdrawalCoinOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.WITHDRAWAL_COIN.getTradeStatus(), BillClassifyDesEnum.WITHDRAWAL_COIN.getTradeType(),
                BillClassifyDesEnum.WITHDRAWAL_COIN.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        withdrawalCoinOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(withdrawalCoinOutAmount);
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.WITHDRAWAL_COIN.getClassifyId() + ""),BillClassifyEnum.WITHDRAWAL_COIN.getBillClassifyName(), withdrawalCoinInAmount, withdrawalCoinOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * rocket互转模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getRocketMutualTransfer(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //rocket互转：进账：rocket转入，出账(绝对值)：转出到rocket
        BigDecimal rocketTransferInAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.ROCKET_TRANSFER_IN.getTradeStatus(), BillClassifyDesEnum.ROCKET_TRANSFER_IN.getTradeType(),
                BillClassifyDesEnum.ROCKET_TRANSFER_IN.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        rocketTransferInAmount = UdunBigDecimalUtil.checkBigDecimal(rocketTransferInAmount);
        BigDecimal rocketTransferOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.ROCKET_TRANSFER_OUT.getTradeStatus(), BillClassifyDesEnum.ROCKET_TRANSFER_OUT.getTradeType(),
                BillClassifyDesEnum.ROCKET_TRANSFER_OUT.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        rocketTransferOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(rocketTransferOutAmount);
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.ROCKET_MUTUAL_TRANSFER.getClassifyId() + ""),BillClassifyEnum.ROCKET_MUTUAL_TRANSFER.getBillClassifyName(), rocketTransferInAmount, rocketTransferOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 质押挖矿模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getPledgeMining(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //质押挖矿：进账：无，出账(绝对值)：保证金交纳
        BigDecimal pledgeMiningInAmount = UdunBigDecimalUtil.checkBigDecimal(null);
        BigDecimal pledgeMiningOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.PLEDGE_MINING.getTradeStatus(), BillClassifyDesEnum.PLEDGE_MINING.getTradeType(),
                BillClassifyDesEnum.PLEDGE_MINING.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        pledgeMiningOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(pledgeMiningOutAmount);
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.PLEDGE_MINING.getClassifyId() + ""),BillClassifyEnum.PLEDGE_MINING.getBillClassifyName(), pledgeMiningInAmount, pledgeMiningOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 智能合约模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getSmartContracts(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //智能合约：进账：卖出cat所得【USDT】+买入cat【CAT】+挂单时间过期退还【CAT、CAG】，
        //         出账(绝对值)：cat挂单冻结【CAT】+买入cat【USDT】+兑入cat手续费【CAG】+挂单手续费-CAG冻结【CAG】
        //卖出cat所得【USDT】进账金额
        BigDecimal sellCatIncomeInAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.SELL_CAT_INCOME.getTradeStatus(), BillClassifyDesEnum.SELL_CAT_INCOME.getTradeType(),
                BillClassifyDesEnum.SELL_CAT_INCOME.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        sellCatIncomeInAmount = UdunBigDecimalUtil.checkBigDecimal(sellCatIncomeInAmount);
        //买入cat【CAT】进账金额
        BigDecimal buyCatInAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.BUY_CAT.getTradeStatus(), BillClassifyDesEnum.BUY_CAT.getTradeType(),
                BillClassifyDesEnum.BUY_CAT.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        buyCatInAmount = UdunBigDecimalUtil.checkBigDecimal(buyCatInAmount);
        //挂单时间过期退还【CAT、CAG】进账金额
        BigDecimal orderTimeOutReturnInAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.ORDER_TIME_OUT_RETURN.getTradeStatus(), BillClassifyDesEnum.ORDER_TIME_OUT_RETURN.getTradeType(),
                BillClassifyDesEnum.ORDER_TIME_OUT_RETURN.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        orderTimeOutReturnInAmount = UdunBigDecimalUtil.checkBigDecimal(orderTimeOutReturnInAmount);
        //cat挂单冻结【CAT】出账金额(绝对值)
        BigDecimal catOrderFreezeOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.CAT_ORDER_FREEZE.getTradeStatus(), BillClassifyDesEnum.CAT_ORDER_FREEZE.getTradeType(),
                BillClassifyDesEnum.CAT_ORDER_FREEZE.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        catOrderFreezeOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(catOrderFreezeOutAmount);
        //买入cat【USDT】出账金额(绝对值)
        BigDecimal buyCatOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.BUY_CAT.getTradeStatus(), BillClassifyDesEnum.BUY_CAT.getTradeType(),
                BillClassifyDesEnum.BUY_CAT.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        buyCatOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(buyCatOutAmount);
        //兑入cat手续费【CAG】出账金额(绝对值)
        BigDecimal buyCatFeeCagOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.BUY_CAT_FEE_CAG.getTradeStatus(), BillClassifyDesEnum.BUY_CAT_FEE_CAG.getTradeType(),
                BillClassifyDesEnum.BUY_CAT_FEE_CAG.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        buyCatFeeCagOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(buyCatFeeCagOutAmount);
        //挂单手续费-CAG冻结【CAG】出账金额(绝对值)
        BigDecimal orderFeeCagFreezeOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.ORDER_FEE_CAG_FREEZE.getTradeStatus(), BillClassifyDesEnum.ORDER_FEE_CAG_FREEZE.getTradeType(),
                BillClassifyDesEnum.ORDER_FEE_CAG_FREEZE.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        orderFeeCagFreezeOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(orderFeeCagFreezeOutAmount);
        //智能合约总进账金额计算
        BigDecimal smartContractsInAmount = UdunBigDecimalUtil.getBigDecimalSum(sellCatIncomeInAmount, buyCatInAmount, orderTimeOutReturnInAmount).setScale(4,BigDecimal.ROUND_DOWN);
        //智能合约总出账金额计算
        BigDecimal smartContractsOutAmount = UdunBigDecimalUtil.getBigDecimalSum(catOrderFreezeOutAmount, buyCatOutAmount, buyCatFeeCagOutAmount, orderFeeCagFreezeOutAmount).setScale(4, BigDecimal.ROUND_DOWN);
        //传参创建返回实体
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.SMART_CONTRACTS.getClassifyId() + ""),BillClassifyEnum.SMART_CONTRACTS.getBillClassifyName(), smartContractsInAmount, smartContractsOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 激活矿机模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getActiveMining(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //激活矿机：进账：无，出账(绝对值)：激活矿机
        BigDecimal activeMiningInAmount = UdunBigDecimalUtil.checkBigDecimal(null);
        BigDecimal activeMiningOutAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.ACTIVE_MINING.getTradeStatus(), BillClassifyDesEnum.ACTIVE_MINING.getTradeType(),
                BillClassifyDesEnum.ACTIVE_MINING.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        activeMiningOutAmount = UdunBigDecimalUtil.checkAbsBigDecimal(activeMiningOutAmount);
        BillDetailRes billDetailRes = createBillDetailRes(Long.parseLong(BillClassifyEnum.ACTIVE_MINING.getClassifyId() + ""),BillClassifyEnum.ACTIVE_MINING.getBillClassifyName(), activeMiningInAmount, activeMiningOutAmount);
        billDetailResList.add(billDetailRes);
    }

    /**
     * 商品出售购买模块
     * @param currencyId 币种id
     * @param req 查询参数
     * @param billDetailResList 展示模块列表
     */
    private void getGoodsSellBuy(Long currencyId,BillAuditReq req,List<BillDetailRes> billDetailResList) {
        //商品出售购买：进账：出售商品，出账(绝对值)：购买商品
        BigDecimal goodsSellAmount = walletTradeOrderMapper.findTradeTotalInAmount(BillClassifyDesEnum.GOODS_SELL.getTradeStatus(), BillClassifyDesEnum.GOODS_SELL.getTradeType(),
                BillClassifyDesEnum.GOODS_SELL.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        goodsSellAmount = UdunBigDecimalUtil.checkBigDecimal(goodsSellAmount);
        BigDecimal goodsBuyAmount = walletTradeOrderMapper.findTradeTotalOutAmount(BillClassifyDesEnum.GOODS_BUY.getTradeStatus(), BillClassifyDesEnum.GOODS_BUY.getTradeType(),
                BillClassifyDesEnum.GOODS_BUY.getDes(), currencyId, req.getStartTime(), req.getEndTime());
        goodsBuyAmount = UdunBigDecimalUtil.checkAbsBigDecimal(goodsBuyAmount);
        BillDetailRes billDetailRes = this.createBillDetailRes(Long.parseLong(BillClassifyEnum.GOODS_SELL_BUY.getClassifyId() + ""),BillClassifyEnum.GOODS_SELL_BUY.getBillClassifyName(), goodsSellAmount, goodsBuyAmount);
        billDetailResList.add(billDetailRes);
    }

    private BillDetailRes createBillDetailRes(Long billId,String billName,BigDecimal billTakeInAmount,BigDecimal billExpenditureAmount){
        BillDetailRes billDetailRes = new BillDetailRes();
        billDetailRes.setBillId(billId);
        billDetailRes.setBillName(billName);
        billDetailRes.setBillTakeInAmount(billTakeInAmount);
        billDetailRes.setBillExpenditureAmount(billExpenditureAmount);
        billDetailRes.setBillDifferenceAmount(billTakeInAmount.subtract(billExpenditureAmount).setScale(4, BigDecimal.ROUND_DOWN));
        return billDetailRes;
    }

    private BillAuditRes createBillAuditRes(BigDecimal takeInAmount,BigDecimal balanceBillTakeInAmount,BigDecimal totalTakeInAmount,
                                            BigDecimal expenditureAmount,BigDecimal balanceBillExpenditureAmount,BigDecimal totalExpenditureAmount,
                                            BigDecimal differenceAmount,Long pledgeMiningPopulation,BigDecimal pledgeMiningAmount) {
        BillAuditRes billAuditRes = new BillAuditRes();
        billAuditRes.setTakeInAmount(takeInAmount);
        billAuditRes.setBalanceBillTakeInAmount(balanceBillTakeInAmount);
        billAuditRes.setTotalTakeInAmount(totalTakeInAmount);
        billAuditRes.setExpenditureAmount(expenditureAmount);
        billAuditRes.setBalanceBillExpenditureAmount(balanceBillExpenditureAmount);
        billAuditRes.setTotalExpenditureAmount(totalExpenditureAmount);
        billAuditRes.setDifferenceAmount(differenceAmount);
        billAuditRes.setPledgeMiningPopulation(pledgeMiningPopulation);
        billAuditRes.setPledgeMiningAmount(pledgeMiningAmount);
        return billAuditRes;
    }

    /**
     * 查看所有展示的账单分类
     * @return
     */
    private List<AdminBillClassify> findShowBillClassify(){
        List<AdminBillClassify> billClassifyList = this.findAll();
        //筛选出未展示的账单分类列表
        List<AdminBillClassify> showBillClassifyList = new ArrayList<>();
        billClassifyList.stream().filter(e->e.getShowState() == 1).forEach(showBillClassifyList::add);
        return showBillClassifyList;
    }

    /**
     * 查看所有的账单分类
     * @return 所有账单分类列表
     */
    private List<AdminBillClassify> findAll() {
        //缓存中获取账单分类表信息
        List<AdminBillClassify> billClassifyList = new ArrayList<>();
        billClassifyList = commonRedisClient.get(RedisConstant.ADMIN_BILL_CLASSIFY);
        if (null == billClassifyList || billClassifyList.size() == 0) {
            billClassifyList = adminBillClassifyMapper.selectList(null);
            commonRedisClient.set(RedisConstant.ADMIN_BILL_CLASSIFY,billClassifyList);
        }
        return billClassifyList;
    }
}
