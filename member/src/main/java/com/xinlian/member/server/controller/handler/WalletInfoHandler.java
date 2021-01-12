package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.dao.TReleaseCatRecordMapper;
import com.xinlian.biz.dao.TUserInfoMapper;
import com.xinlian.biz.dao.TrcWalletInfoMapper;
import com.xinlian.biz.model.*;
import com.xinlian.common.enums.*;
import com.xinlian.common.request.WithdrawBudgetServiceFeeRequest;
import com.xinlian.common.request.WithdrawCurrencyRequest;
import com.xinlian.common.result.BizException;
import com.xinlian.common.result.ErrorInfoEnum;
import com.xinlian.common.scedule.WithdrawTradeSuccessLogSave;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.common.utils.UniqueNoUtil;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.*;
import com.xinlian.member.server.vo.response.wallet.CurrencyAddressResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

@Component
@Slf4j
public class WalletInfoHandler {

    @Autowired
    private IServerNodeService serverNodeService;
    @Autowired
    private TCurrencyManageService currencyManageService;
    @Autowired
    private TWalletTradeOrderService walletTradeOrderService;
    @Autowired
    private TWalletInfoService walletInfoService;
    @Autowired
    private TUserInfoMapper userInfoMapper;
    @Autowired
    private TReleaseCatRecordMapper releaseCatRecordMapper;
    @Autowired
    private PushNoticeService pushNoticeService;
    @Autowired
    private WithdrawCustomerService withdrawCustomerService;
    @Autowired
    private JudgeSeaPatrolTradeRuleHandler judgeSeaPatrolTradeRuleHandler;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private WithdrawTradeSuccessLogSave withdrawTradeSuccessLogSave;
    @Autowired
    private TrcWalletInfoMapper trcWalletInfoMapper;


    /**
     * 提币接口处理类入口
     * @param withdrawCurrencyRequest
     */
    @Transactional
    public void withdrawHandler(WithdrawCurrencyRequest withdrawCurrencyRequest){
        boolean withdrawCustomerUidFlag = false;
        WithdrawCustomerModel withdrawCustomerModel = null;
        TServerNode toServerNode = this.judgeInsideOrOutSideServerNode(withdrawCurrencyRequest);
        if(null==toServerNode
                && CurrencyEnum.USDT.getCurrencyId()==Integer.parseInt(withdrawCurrencyRequest.getCoin_id())){
            withdrawCustomerUidFlag = withdrawCustomerService.checkWithdrawCustomerUid(withdrawCurrencyRequest.getUserId());
            //1.检查UID是否存在 容许客户名单中得客户信息，如果存在就走另外得分支判断
            withdrawCustomerModel = withdrawCustomerService.judgeWithdrawCustomer(withdrawCurrencyRequest);
        }
        //UID 集合不过
        //2.币种管理与节点下等相关判断 -- Uid是特殊集合中（注：需要满足额度），就不走币种管理判断
        log.info("提币申请:存在标识[{}],额度是否超过判断[{}]",withdrawCustomerUidFlag,null==withdrawCustomerModel);
        if(!withdrawCustomerUidFlag ){
            if(judgeSeaPatrolTradeRuleHandler.checkBothIsSeaPatrol(withdrawCurrencyRequest,toServerNode)) {
                this.tradsferAccountsMaxJudge(withdrawCurrencyRequest, toServerNode);
            }
        }else if(withdrawCustomerUidFlag && null==withdrawCustomerModel){
            try {
                if(judgeSeaPatrolTradeRuleHandler.checkBothIsSeaPatrol(withdrawCurrencyRequest,toServerNode)) {
                    this.tradsferAccountsMaxJudge(withdrawCurrencyRequest, toServerNode);
                }
            }catch (BizException e) {
                //存在集合id,但不额度不够
                throw new BizException("暂未开放");
            }
        }
        if(null == toServerNode
                && CurrencyEnum.USDT.getCurrencyId()==Integer.parseInt(withdrawCurrencyRequest.getCoin_id())){
            //进行减准许提现额度
            if(null!=withdrawCustomerModel){
                withdrawCustomerModel.setAllowWithdrawNum(new BigDecimal(withdrawCurrencyRequest.getNum()));
                withdrawCustomerService.updateModel(withdrawCustomerModel);
            }
            //提币-入库等待运营后台进行审核
            disposeWallInfoFreezeAndCreateTradeOrder(withdrawCurrencyRequest);
        }else if(null != toServerNode){
            //内部转账
            judgeSeaPatrolTradeRuleHandler.doCheckFormServerNode(withdrawCurrencyRequest,toServerNode);
            internalTransfer(withdrawCurrencyRequest);
        }else{
            throw new BizException("请核实交易情况!");
        }
    }

    private TWalletInfo getWalletInfoModel(Long uid,Long currencyId,String currencyAddress){
        TWalletInfo whereModel = new TWalletInfo();
        whereModel.setUid(uid);
        whereModel.setCurrencyId(currencyId);
        whereModel.setCurrencyAddress(currencyAddress);
        return walletInfoService.getByCriteria(whereModel);
    }

    //内部转账
    @Transactional(rollbackFor = Throwable.class)
    public void internalTransfer(WithdrawCurrencyRequest withdrawCurrencyRequest) {
        //1.拿出交易双方客户信息 - 存入交易单中
        //获取转出钱包信息
        Long currencyId = Long.parseLong(withdrawCurrencyRequest.getCoin_id());
        TWalletInfo fromWalletInfo = this.getWalletInfoModel(withdrawCurrencyRequest.getUserId(),currencyId,null);
        //判断是否转出地址跟转出交易方地址一样
        if(withdrawCurrencyRequest.getAddress().toLowerCase().equals(fromWalletInfo.getCurrencyAddress().toLowerCase())){
            throw new BizException(ErrorInfoEnum.WALLET_INFO_TRADE_ERROR_TURN_YOURSELF);
        }
        //检验空投之锁仓
        this.checkLockedPosition(fromWalletInfo,withdrawCurrencyRequest);
        //1.1获取转入账户信息
        TWalletInfo toWalletInfo = this.getWalletInfoModel(null,currencyId,withdrawCurrencyRequest.getAddress());
        //1.2获取转出，转入客户信息
        TUserInfo fromUserInfo = userInfoMapper.selectById(withdrawCurrencyRequest.getUserId());
        TUserInfo toUserInfo = userInfoMapper.selectById(toWalletInfo.getUid());
        if(UserLevelStatusEnum.FREEZE.getCode() == toUserInfo.getLevelStatus()){
            throw new BizException("抱歉，对方账户已被冻结!");
        }
        //记录 转出订单
        TWalletTradeOrder insideTradeOrder = this.createRollOffWalletTradeOrder(withdrawCurrencyRequest, WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode(),toUserInfo);
        //扣减余额
        log.info("提币动作之前转出账户余额~~~~~~:" + fromWalletInfo.getBalanceNum());
        //fromWalletInfo.setBalanceNum(UdunBigDecimalUtil.subNumAndCheckIsZero(fromWalletInfo.getBalanceNum(),new BigDecimal(withdrawCurrencyRequest.getNum())));
        log.info("提币动作之后转出账户余额~~~~~~:" + fromWalletInfo.getBalanceNum());
        log.info("提币动作之后转出账户交易金额~~~~~~:" + withdrawCurrencyRequest.getNum());
        //变动资产
        fromWalletInfo.setMovableAssetsNum(new BigDecimal(withdrawCurrencyRequest.getNum()));
        int fromWalletInfoResultNum = walletInfoService.fromWalletInfoAbatmentBalanceNum(fromWalletInfo);
        //记录 转入订单
        //获取转入钱包信息 增加余额
        int shiftToResultNum = this.createShiftToWalletTradeOrder(withdrawCurrencyRequest,toWalletInfo.getUid(), WalletTradeOrderStatusEnum.TRADE_SUCCESS.getCode(),fromUserInfo,fromWalletInfo.getCurrencyAddress());
        //转入订单 - 余额 来自 转出币种数量减去费用
        BigDecimal intoTheCost = UdunBigDecimalUtil.subtractNum(new BigDecimal(withdrawCurrencyRequest.getNum()),insideTradeOrder.getTradeFee());
        log.info("提币动作之前转入账户余额~~~~~~:"+toWalletInfo.getBalanceNum());
        //转入订单 余额 + 转入账户之前余额
        //toWalletInfo.setBalanceNum(UdunBigDecimalUtil.addNum(toWalletInfo.getBalanceNum(),intoTheCost));
        log.info("提币动作之后转入账户余额~~~~~~:"+toWalletInfo.getBalanceNum());
        log.info("提币动作之后转入账户交易金额~~~~~~:" + withdrawCurrencyRequest.getNum());
        toWalletInfo.setMovableAssetsNum(intoTheCost);
        int toWalletInfoResultNum = walletInfoService.toWalletInfoAddBalanceNum(toWalletInfo);
        if(fromWalletInfoResultNum==0||shiftToResultNum==0||toWalletInfoResultNum==0){
            throw new BizException("内部转账交易出现异常!");
        }
        //异步调用
        pushNoticeService.saveAppNoticePushRecord(toUserInfo.getUid(),toUserInfo.getJid(),toWalletInfo.getCurrencyCode(),withdrawCurrencyRequest.getNum(),JPushTitleMessageEnum.TRANSFER_SUCCESS);
        //账户大额变动流水记录
        String uuid = UniqueNoUtil.uuid();
        //转账方
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(fromWalletInfo.getUid(),currencyId,insideTradeOrder.getCurrencyCode(),insideTradeOrder.getCounterPartyUid(),insideTradeOrder.getTradeAddress(),
                insideTradeOrder.getTradeCurrencyNum(),insideTradeOrder.getDes(),null,new Date(),uuid);
        //收款方
        withdrawTradeSuccessLogSave.addWithdrawTradeSuccessLog(toWalletInfo.getUid(),currencyId,insideTradeOrder.getCurrencyCode(),fromWalletInfo.getUid(),fromWalletInfo.getCurrencyAddress(),
                UdunBigDecimalUtil.convertBigDecimal(withdrawCurrencyRequest.getNum()),WalletTradeTypeEnum.INTERNAL_TRADE_ADD.getTradeDesc(),null,new Date(),uuid);
    }

    /**
     * 提币判断 - 之空投100cag 进行锁仓，不让提币出来 20200318 18:40:40
     * @param fromWalletInfo 钱包bean
     * @param withdrawCurrencyRequest 提币数量
     */
    public void checkLockedPosition(TWalletInfo fromWalletInfo,WithdrawCurrencyRequest withdrawCurrencyRequest) {
        //糖果节点并且是CAG操作
        if(107==withdrawCurrencyRequest.getServerNodeId().intValue()
                && CurrencyEnum.CAG.getCurrencyId()==fromWalletInfo.getCurrencyId().intValue()){
            if(releaseCatRecordMapper.checkLockedPositionIsUId(fromWalletInfo.getUid())==0){return;}
            BigDecimal numValue = new BigDecimal(withdrawCurrencyRequest.getNum());
            BigDecimal flagValue = UdunBigDecimalUtil.subtractNum(fromWalletInfo.getBalanceNum(),numValue);
            BigDecimal hundredValue = new BigDecimal("100");
            if(flagValue.compareTo(hundredValue)<0){
                throw new BizException(ErrorInfoEnum.WITH_DRAW_LOCKED_POSITION);
            }
        }
    }

    /**
     * 创建交易记录 -冻结钱包中的交易金额
     * 1.先冻结资金 - 2.成功后 - 3.创建交易单
     * @param withdrawCurrencyRequest
     * @return
     */
    @Transactional
    public TWalletTradeOrder disposeWallInfoFreezeAndCreateTradeOrder(WithdrawCurrencyRequest withdrawCurrencyRequest) {
        //处理钱包余额金额与冻结金额
        int rowNum = walletInfoService.disposalBalanceAndFreeze
                (withdrawCurrencyRequest.getUserId(),Long.parseLong(withdrawCurrencyRequest.getCoin_id()),new BigDecimal(withdrawCurrencyRequest.getNum()));
        if(rowNum<1){
            log.error(JSONObject.toJSONString(withdrawCurrencyRequest));
            throw new BizException("提币处理金额扣减不足，请核对!");
        }
        //创建钱包交易订单
        return this.createWalletTradeOrderByWithdraw(withdrawCurrencyRequest, WalletTradeOrderStatusEnum.APPLY.getCode());
    }

    /**
     * 提币-创建钱包交易订单
     */
    public TWalletTradeOrder createWalletTradeOrderByWithdraw(WithdrawCurrencyRequest withdrawCurrencyRequest, Integer tradeStatus) {
        TWalletTradeOrder walletTradeOrder = createWalletTradeOrder(withdrawCurrencyRequest,WalletTradeTypeEnum.MENTION_MONEY.getTradeDesc(),tradeStatus);
        walletTradeOrderService.saveWalletTradeOrder(walletTradeOrder);
        return walletTradeOrder;
    }

    /**
     * 内部转账之转出-创建钱包交易订单
     * @param withdrawCurrencyRequest 请求参数
     * @param tradeStatus 交易状态
     * @param toUserInfo 转出 - 对方信息model
     * @return
     */
    public TWalletTradeOrder createRollOffWalletTradeOrder(WithdrawCurrencyRequest withdrawCurrencyRequest, Integer tradeStatus,TUserInfo toUserInfo) {
        TWalletTradeOrder getTradeOrder = createWalletTradeOrder(withdrawCurrencyRequest,WalletTradeTypeEnum.INTERNAL_TRADE_TO.getTradeDesc(),tradeStatus);
        getTradeOrder.setCounterPartyMobile(toUserInfo.getMobile());
        getTradeOrder.setCounterPartyUserName(toUserInfo.getUserName());
        getTradeOrder.setCounterPartyUid(toUserInfo.getUid());
        int insideResultNum =  walletTradeOrderService.saveWalletTradeOrder(getTradeOrder);
        if(insideResultNum == 0 ){
            throw new BizException("内部转账交易出现异常!");
        }
        return getTradeOrder;
    }

    /**
     * 内部转账之转入-创建钱包交易订单
     */
    public Integer createShiftToWalletTradeOrder(WithdrawCurrencyRequest withdrawCurrencyRequest, Long toUid, Integer tradeStatus,TUserInfo userInfo,String fromCurrencyAddress) {
        TWalletTradeOrder getTradeOrder = createWalletTradeOrder(withdrawCurrencyRequest,WalletTradeTypeEnum.INTERNAL_TRADE_ADD.getTradeDesc(),tradeStatus);
        getTradeOrder.setUid(toUid);
        getTradeOrder.setCounterPartyUid(userInfo.getUid());
        getTradeOrder.setCounterPartyMobile(userInfo.getMobile());
        getTradeOrder.setCounterPartyUserName(userInfo.getUserName());
        getTradeOrder.setTradeAddress(fromCurrencyAddress);//转入记录 - 记录付款方交易地址
        return walletTradeOrderService.saveWalletTradeOrder(getTradeOrder);
    }
    /**
     * 创建钱包转账订单 - 基础
     */
    public TWalletTradeOrder createWalletTradeOrder(WithdrawCurrencyRequest withdrawCurrencyRequest, String tradsferTypeDesc, Integer tradeStatus) {
        String currencyId = withdrawCurrencyRequest.getCoin_id();
        String currencyCode = CurrencyEnum.getCurrencyCodeByCurrencyId(currencyId);
        TWalletTradeOrder walletTradeOrder = new TWalletTradeOrder();
        walletTradeOrder.setCurrencyId(Long.parseLong(currencyId));
        walletTradeOrder.setCurrencyCode(currencyCode);
        walletTradeOrder.setTradeAddress(withdrawCurrencyRequest.getAddress());
        this.convertWalletTradeOrder(withdrawCurrencyRequest,tradsferTypeDesc,walletTradeOrder);
        walletTradeOrder.setTradeStatus(tradeStatus);
        walletTradeOrder.setTradeType(WalletTradeTypeEnum.getEnumTradeType(tradsferTypeDesc));
        walletTradeOrder.setDes(tradsferTypeDesc);
        return walletTradeOrder;
    }

    /**
     * 根据提币动作 - 以及币种设置规则计算交易费用及转账币种数量
     * @param withdrawCurrencyRequest
     * @param tradsferTypeDesc
     * @param walletTradeOrder
     */
    private void convertWalletTradeOrder(WithdrawCurrencyRequest withdrawCurrencyRequest, String tradsferTypeDesc, TWalletTradeOrder walletTradeOrder){
        //提币、内部转账因设置不同，产生费用也不同
        //获取币种信息
        TCurrencyManage currencyManage = currencyManageService.getCurrencyManageByCurrencyId(Long.parseLong(withdrawCurrencyRequest.getCoin_id()));
        BigDecimal tradeFee = null;
        if(WalletTradeTypeEnum.MENTION_MONEY.getTradeDesc().equals(tradsferTypeDesc)){
            walletTradeOrder.setUid(withdrawCurrencyRequest.getUserId());
            tradeFee = this.getWithdrawServiceFree(currencyManage,new BigDecimal(withdrawCurrencyRequest.getNum()));
            walletTradeOrder.setTradeCurrencyNum(UdunBigDecimalUtil.convertMinus(withdrawCurrencyRequest.getNum()));
        }else if(WalletTradeTypeEnum.INTERNAL_TRADE_TO.getTradeDesc().equals(tradsferTypeDesc)){
            walletTradeOrder.setUid(withdrawCurrencyRequest.getUserId());
            tradeFee = this.getInsideTransferAccountsServiceFree(currencyManage,new BigDecimal(withdrawCurrencyRequest.getNum()));
            walletTradeOrder.setTradeCurrencyNum(UdunBigDecimalUtil.convertMinus(withdrawCurrencyRequest.getNum()));
        }else {
            walletTradeOrder.setTradeCurrencyNum(UdunBigDecimalUtil.convertBigDecimal(withdrawCurrencyRequest.getNum()));
            tradeFee = UdunBigDecimalUtil.zeroBigDecimal; //内部转账-转入
        }
        if(new BigDecimal(withdrawCurrencyRequest.getNum()).compareTo(tradeFee)<0){
            throw new BizException(ErrorInfoEnum.CURRENCY_NUM_LESS_TRADE_FEE);
        }
        walletTradeOrder.setTradeFee(tradeFee);
    }

    /**
     * 获取提币手续费
     * @return
     */
    private BigDecimal getWithdrawServiceFree(TCurrencyManage currencyManage,BigDecimal withdrawNum){
        if(CurrencyServiceFeeEnum.FIXATION.getCode() == currencyManage.getCashfeeStatus()){ //固定值
            return currencyManage.getCashFee();
        }else{
            return UdunBigDecimalUtil.getMinFee(withdrawNum,currencyManage.getCashFee(),currencyManage.getCashfeeMin());
        }
    }

    /**
     * 获取内部转账手续费
     * @param currencyManage
     * @param withdrawNum
     * @return
     */
    private BigDecimal getInsideTransferAccountsServiceFree(TCurrencyManage currencyManage,BigDecimal withdrawNum){
        if(CurrencyServiceFeeEnum.FIXATION.getCode() == currencyManage.getInsideTradeStatus()){ //固定值
            return currencyManage.getInsideTradeFee();
        }else{
            return UdunBigDecimalUtil.getMinFee(withdrawNum,currencyManage.getInsideTradeFee(),currencyManage.getInsideTradeMin());
        }
    }

    /**
     * 提币前大判断逻辑汇总
     * @return
     */
    public boolean tradsferAccountsMaxJudge(WithdrawCurrencyRequest withdrawCurrencyRequest,TServerNode toServerNode){
        Long currencyId = Long.parseLong(withdrawCurrencyRequest.getCoin_id());
        String fromCurrencyCode = CurrencyEnum.getEnum(currencyId).getCurrencyCode();
        String toCurrencyAddress = withdrawCurrencyRequest.getAddress();
        //1.获取转出serverNode 以此
        TServerNode transferTheServerNode = serverNodeService.getById(withdrawCurrencyRequest.getServerNodeId());
        log.info("转出serverNode:{}",JSONObject.toJSONString(transferTheServerNode));
        if(null==toServerNode){//外部地址
            //1.1判断币种是否允许提现
            if(!this.checkCurrencyInfoWithdrawPermissions(currencyId)){
                log.warn("不可转账-uid:{},currencyId:{},外部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_3.getMsg());
                throw new BizException(ErrorInfoEnum.CURRENCY_CODE_OPEN_WITHDRAW);
            }else if(!checkTransferTheServerNodeOutSideServer(transferTheServerNode)){
                log.warn("不可转账-uid:{},currencyId:{},外部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_1.getMsg());
                throw new BizException(ErrorInfoEnum.CURRENCY_CODE_OPEN_WITHDRAW);
            }else if(!judgeFromServerNodeCash(transferTheServerNode,fromCurrencyCode)) {
                //判断转出节点 是否可以提现
                log.warn("不可转账-uid:{},currencyId:{},外部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_5.getMsg());
                throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_CASH);
            }else {
                return true;
            }
        }else {//内部地址
            //3.获取客户转出所在币对应的币种管理信息，判断是否可以对内转出
            if(!checkCurrencyInfoTransferPermissions(currencyId)) { //币种管理下不容许内部转账
                log.warn("不可转账-uid:{},currencyId:{},内部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.CURRENCY_INTERNAL_TRANSFER.getMsg());
                throw new BizException(ErrorInfoEnum.CURRENCY_INTERNAL_TRANSFER);
            }else if(!judgeFromServerNodeCash(transferTheServerNode,fromCurrencyCode)) {
                //判断转出节点 是否可以提现
                log.warn("不可转账-uid:{},currencyId:{},内部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_5.getMsg());
                throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_CASH);
            }else if(!judgeToServerRecharge(toServerNode,fromCurrencyCode)){
                //转入节点 是否可以充值
                log.warn("不可转账-uid:{},currencyId:{},内部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_4.getMsg());
                throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_RECHARGE.getMsg());
            }else if (!judgeFromServerNodeTransfer(transferTheServerNode,toServerNode,fromCurrencyCode) && !judgeToServerNodeTransfer(transferTheServerNode,toServerNode,fromCurrencyCode)) {
                //4.转出节点、转入节点 内部转账判断
                log.warn("不可转账-uid:{},currencyId:{},内部地址-toCurrencyAddress:{}-原因：{}",
                        withdrawCurrencyRequest.getUserId(),currencyId,toCurrencyAddress,ErrorInfoEnum.WALLET_INFO_TRADE_ORDER_2.getMsg());
                throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_TRANSFERS_BETWEEN);
            }else {
                return true;
            }
        }
    }

    private boolean judgeFromServerNodeCash(TServerNode fromServerNode, String fromCurrencyCode) {
        int cashStatus = getServerNodeCashStatus(fromServerNode,fromCurrencyCode);
        return ServerNodeWithdrawStatusEnum.checkIsWithdrawFlag(cashStatus);
    }

    public boolean judgeToServerRecharge(TServerNode toServerNode, String fromCurrencyCode) {
        int rechargeStatus = getServerNodeRechargeStatus(toServerNode,fromCurrencyCode);
        return ServerNodeWithdrawStatusEnum.checkIsWithdrawFlag(rechargeStatus);
    }

    /**
     * 根据转出节点信息 判断该节点是否可以对外提现操作
     * 可以转出返回true ,不可以对外转出返回false
     * @param transferTheServerNode
     * @return
     */
    private boolean checkTransferTheServerNodeOutSideServer(TServerNode transferTheServerNode) {
        return ServerNodeWithdrawStatusEnum.checkIsWithdrawFlag(transferTheServerNode.getWithdrawStatus());
    }

    /**
     * 根据uid.转出地址判断是否为内外地址
     * @param currencyId
     * @param toCurrencyAddress
     */
    public TServerNode judgeInsideOrOutSideServerNode(Long currencyId, String toCurrencyAddress) {
        if(StringUtils.isBlank(toCurrencyAddress)){
            throw new BizException("参数异常!");
        }
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "TRADE_ADDRESS_"+ toCurrencyAddress;
        String serverNodeId = redisClient.get(redisKey);
        if(StringUtils.isBlank(serverNodeId)) {
            //1.根据uid，转账地址获取对应地址 - 走缓存
            TWalletInfo walletInfo = new TWalletInfo();
            walletInfo.setCurrencyId(currencyId);
            walletInfo.setCurrencyAddress(toCurrencyAddress);
            TServerNode returnObj = serverNodeService.getServerNodeByWithdrawAddress(walletInfo);
            if(returnObj == null){return null;}
            serverNodeId = returnObj.getId().toString();
            redisClient.setDay(redisKey,serverNodeId,10);
        }
        return serverNodeService.getById(Long.parseLong(serverNodeId));
    }

    public TServerNode judgeInsideOrOutSideServerNode(WithdrawCurrencyRequest request) {
        return this.judgeInsideOrOutSideServerNode(Long.parseLong(request.getCoin_id()),request.getAddress());
    }

    /**
     * 检查币种管理 - 币种是否可以转账
     * @param currencyId
     * @return
     */
    public boolean checkCurrencyInfoTransferPermissions(Long currencyId){
        TCurrencyManage currencyManage = currencyManageService.getCurrencyManageByCurrencyId(currencyId);
        //币种管理下的是否转账判断
        return CurrencyManageIsintertrEnum.checkIsIntertrFlag(currencyManage.getIsintertr());
    }

    /**
     * 检查币种管理 - 是否允许提现
     * @param currencyId
     * @return
     */
    public boolean checkCurrencyInfoWithdrawPermissions(Long currencyId){
        TCurrencyManage currencyManage = currencyManageService.getCurrencyManageByCurrencyId(currencyId);
        //币种管理下的是否允许提现
        return CurrencyManageIsintertrEnum.checkIsIntertrFlag(currencyManage.getCash());
    }

    /**
     * 检查币种管理 - 是否允许充值
     * @param currencyId
     * @return
     */
    public boolean checkCurrencyInfoRechargePermissions(Long currencyId){
        TCurrencyManage currencyManage = currencyManageService.getCurrencyManageByCurrencyId(currencyId);
        //币种管理下的是否允许充值
        return CurrencyManageIsintertrEnum.checkIsIntertrFlag(currencyManage.getRecharge());
    }

    /**
     * 判断转出节点 内部转账是否支持
     * @param fromServerNode
     * @param toServerNode
     * @param fromCurrencyCode
     * @return
     */
    public boolean judgeFromServerNodeTransfer(TServerNode fromServerNode,TServerNode toServerNode,String fromCurrencyCode){
        //获取客户对应节点下某个币种code值来判断是否可以转账
        return judgeServerNodeTransfer(fromServerNode,toServerNode,fromCurrencyCode);
    }

    /**
     * 判断转入节点 内部转账是否支持
     * @param fromServerNode
     * @param toServerNode
     * @param fromCurrencyCode
     * @return
     */
    public boolean judgeToServerNodeTransfer(TServerNode fromServerNode,TServerNode toServerNode,String fromCurrencyCode){
        //获取客户对应节点下某个币种code值来判断是否可以转账
        return judgeServerNodeTransfer(toServerNode,fromServerNode,fromCurrencyCode);
    }

    public boolean judgeServerNodeTransfer(TServerNode stayServerNode,TServerNode criterionServerNode,String fromCurrencyCode){
        Integer transferCurrencyStatus = getServerNodeRollOut(stayServerNode,fromCurrencyCode);
        if(transferCurrencyStatus.intValue() == TransferStatusEnum.TRANSFER_ALL.getCode()){
            //节点全部支持
            return true;
        }else if(transferCurrencyStatus.intValue() == TransferStatusEnum.TRANSFER_INSTATION.getCode()){
            //只支持同节点
            if(stayServerNode.getId().intValue() == criterionServerNode.getId().intValue()){
                return true;
            }
            throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_TRANSFERS_BETWEEN_ERROR,"不同节点",true);
        }else if(transferCurrencyStatus.intValue() == TransferStatusEnum.TRANSFER_EXTERNAL.getCode()){
            //只支持不同节点
            if(stayServerNode.getId().intValue() != criterionServerNode.getId().intValue()){
                return true;
            }
            throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_TRANSFERS_BETWEEN_ERROR,"同节点",true);
        }else {
            //全部不支持
            throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_TRANSFERS_BETWEEN_ALL_ERROR);
        }
    }

    /**
     * 判断转出节点 某个币种code是否可以提现
     * @param serverNode 节点信息对象
     * @param fromCurrencyCode 币种code
     * @return
     */
    public int getServerNodeCashStatus(TServerNode serverNode,String fromCurrencyCode){
        String targetFieldName = getServerNodeFieldName("cash",fromCurrencyCode,"Status");
        Integer fieldValue = this.getFieldValueByObject(serverNode,targetFieldName,Integer.class);
        if(null==fieldValue){throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_CASH);}
        return fieldValue;
    }

    /**
     * 根据币种code，获取该节点该币种的转账设置
     * @param serverNode 节点信息
     * @param fromCurrencyCode 币种code
     * @return
     */
    public Integer getServerNodeRollOut(TServerNode serverNode, String fromCurrencyCode) {
        String targetFieldName = getServerNodeFieldName("transfer",fromCurrencyCode,"Status");
        Integer fieldValue = this.getFieldValueByObject(serverNode,targetFieldName,Integer.class);
        if(null==fieldValue){throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_TRANSFERS_BETWEEN);}
        return fieldValue;
    }

    /**
     * 根据币种code，获取该节点该币种的充值设置
     * @param serverNode 节点信息
     * @param fromCurrencyCode 币种code
     * @return
     */
    public Integer getServerNodeRechargeStatus(TServerNode serverNode, String fromCurrencyCode) {
        String targetFieldName = getServerNodeFieldName("recharge",fromCurrencyCode,"Status");
        Integer fieldValue = this.getFieldValueByObject(serverNode,targetFieldName,Integer.class);
        if(null==fieldValue){throw new BizException(ErrorInfoEnum.SERVER_NODE_NOT_RECHARGE);}
        return fieldValue;
    }

    private <T> T getFieldValueByObject(Object object, String targetFieldName,Class<T> clazz) {
        Class objClass = object.getClass();
        Field[] fields = objClass.getDeclaredFields();
        Object resultFileValue = null;
        try {
            for (Field field : fields) {
                // 属性名称
                String currentFieldName = field.getName();
                if (currentFieldName.equals(targetFieldName)) {
                    field.setAccessible(true);
                    resultFileValue = field.get(object);
                 }
            }
        }catch(Exception e){
            log.error("getFieldValueByObject反射出现异常:", e.toString(), e);
        }
        if(null==resultFileValue){ return null;}
        return (T)resultFileValue;
    }

    /**
     * 组装字段
     * @param prefix 前缀
     * @param fromCurrencyCode 币种code
     * @param suffix 后缀
     * @return
     */
    private static String getServerNodeFieldName(String prefix, String fromCurrencyCode,String suffix){
        StringBuffer resultStr = new StringBuffer(prefix);
        resultStr.append(fromCurrencyCode.toUpperCase().substring(0,1));
        resultStr.append(fromCurrencyCode.toLowerCase().substring(1,fromCurrencyCode.length()));
        resultStr.append(suffix);
        return resultStr.toString();
    }


    public BigDecimal budgetServiceFeeRequest(WithdrawBudgetServiceFeeRequest budgetServiceFeeRequest) {
        //1.判断转出地址是否内、外部地址 -
        TServerNode toServerNode = judgeInsideOrOutSideServerNode(Long.parseLong(budgetServiceFeeRequest.getCoin_id()),budgetServiceFeeRequest.getAddress());
        BigDecimal tradeFee = null;
        TCurrencyManage currencyManage = currencyManageService.getCurrencyManageByCurrencyId(Long.parseLong(budgetServiceFeeRequest.getCoin_id()));
        if(null==toServerNode) {//外部地址
            tradeFee = this.getWithdrawServiceFree(currencyManage,new BigDecimal(budgetServiceFeeRequest.getNum()));
        }else{ //内部地址
            tradeFee = this.getInsideTransferAccountsServiceFree(currencyManage,new BigDecimal(budgetServiceFeeRequest.getNum()));
        }
        return tradeFee;
    }

    public CurrencyAddressResponse getUserWalletInfoAddressByCurrencyId(Long userId, int currencyId) {
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "TRC_ADDRESS_" + userId;
        CurrencyAddressResponse currencyAddressResponse = redisClient.get(redisKey);
        if(currencyAddressResponse==null
                || null==currencyAddressResponse.getBasicAddress()
                || null==currencyAddressResponse.getTrcUsdtAddress()){
            currencyAddressResponse = new CurrencyAddressResponse();
            TWalletInfo walletInfo = new TWalletInfo();
            walletInfo.setUid(userId);
            walletInfo.setCurrencyId(Long.valueOf(currencyId));
            TWalletInfo getWalletInfo = walletInfoService.getByCriteriaNoLock(walletInfo);
            TrcWalletInfoModel whereTrcModel = new TrcWalletInfoModel();
            whereTrcModel.setUid(userId);
            TrcWalletInfoModel trcWalletInfoModel = trcWalletInfoMapper.getTrcWalletInfo(whereTrcModel);
            if(null==getWalletInfo || null == getWalletInfo.getCurrencyAddress()){
                currencyAddressResponse.setBasicAddress(null);
            }else{
                currencyAddressResponse.setBasicAddress(getWalletInfo.getCurrencyAddress());
            }
            if(null==trcWalletInfoModel || null == trcWalletInfoModel.getCurrencyAddress()){
                currencyAddressResponse.setTrcUsdtAddress(null);
            }else{
                currencyAddressResponse.setTrcUsdtAddress(trcWalletInfoModel.getCurrencyAddress());
            }
            currencyAddressResponse.setBasicAddress(getWalletInfo.getCurrencyAddress());
            redisClient.setDay(redisKey,currencyAddressResponse,8);
        }
        return currencyAddressResponse;
    }

    /**
     * 检查该客户钱包地址是否已分配
     * optimize 走redis
     * @param userId
     * @return
     */
    public boolean checkCurrencyAddressStatus(Long userId) {
        String redisValue = this.getUserWalletInfoAddressByCurrencyId(userId,CurrencyEnum.USDT.getCurrencyId()).getBasicAddress();
        return null!=redisValue;
    }
    @Autowired
    private IAddressPoolService addressPoolService;
    /**
     * 分配客户钱包地址
     * @param userId
     */
    @Transactional
    public CurrencyAddressResponse allocationCurrencyAddress(Long userId) {
        //1.获取未消费-未分配优盾地址
        TAddressPool addressPool = addressPoolService.undistributed();
        if(null==addressPool){
            log.warn("分配客户钱包地址:未获取到待分配的地址");
            throw new BizException("地址正在生成，请稍后再试!");
        }
        //2.消费地址成功插入到钱包中
        int resultNum = walletInfoService.allocationCurrencyAddress(userId,addressPool.getAddress());
        if(resultNum == 0){
            log.warn("分配客户钱包地址出现异常!userId:{},address:{}",userId,addressPool.getAddress());
            throw new BizException("地址正在生成，请稍后再试!");
        }
        //更新 - 之前地址
        addressPool.setStatus(AddressAllotStatusEnum.ALREADY.getCode());
        addressPool.setOldStatus(AddressAllotStatusEnum.HAVE_NOT.getCode());
        int updateResultNum = addressPoolService.updateModel(addressPool);
        if(updateResultNum==0){
            log.warn("分配客户钱包地址出现异常!");
            throw new BizException("地址正在生成，请稍后再试!");
        }
        //分配客户钱包地址 - TRC20_USDT
        //1.获取未消费-未分配TRC地址
        TrcUsdtAddressPool trcUsdtAddressPool = trcUsdtAddressPoolService.undistributed();
        if(null==trcUsdtAddressPool){
            log.warn("【TRC20_USDT】分配客户钱包地址:未获取到待分配的地址");
            throw new BizException("地址正在生成，请稍后再试!");
        }
        //2.消费地址成功插入到钱包中
        TrcWalletInfoModel trcWalletInfoModel = this.packageTrcWalletInfoModel(userId,trcUsdtAddressPool);
        int trcResultNum = trcWalletInfoMapper.allocationCurrencyAddress(trcWalletInfoModel);
        if(trcResultNum == 0){
            log.warn("【TRC20_USDT】分配客户钱包地址出现异常!userId:{},address:{}",userId,trcUsdtAddressPool.getAddressBase58());
            throw new BizException("地址正在生成，请稍后再试!");
        }
        //更新 - 【TRC地址】
        trcUsdtAddressPool.setStatus(AddressAllotStatusEnum.TRC_ALREADY.getCode());
        trcUsdtAddressPool.setOldStatus(AddressAllotStatusEnum.TRC_HAVE_NOT.getCode());
        int trcUpdateResultNum = trcUsdtAddressPoolService.updateModel(trcUsdtAddressPool);
        if(trcUpdateResultNum==0){
            log.warn("【TRC20_USDT】分配客户钱包地址出现异常!");
            throw new BizException("地址正在生成，请稍后再试!");
        }
        CurrencyAddressResponse currencyAddressResponse = new CurrencyAddressResponse();
        currencyAddressResponse.setBasicAddress(addressPool.getAddress());
        currencyAddressResponse.setTrcUsdtAddress(trcUsdtAddressPool.getAddressBase58());
        return currencyAddressResponse;
    }

    @Autowired
    private TrcUsdtAddressPoolService trcUsdtAddressPoolService;



    private TrcWalletInfoModel packageTrcWalletInfoModel(Long userId,TrcUsdtAddressPool trcUsdtAddressPool){
        TrcWalletInfoModel trcWalletInfoModel = new TrcWalletInfoModel();
        trcWalletInfoModel.setUid(userId);
        trcWalletInfoModel.setCurrencyId(Long.parseLong(CurrencyEnum.USDT.getCurrencyId()+""));
        trcWalletInfoModel.setCurrencyCode(CurrencyEnum.USDT.getCurrencyCode());
        trcWalletInfoModel.setCurrencyAddress(trcUsdtAddressPool.getAddressBase58());
        return trcWalletInfoModel;
    }

}
