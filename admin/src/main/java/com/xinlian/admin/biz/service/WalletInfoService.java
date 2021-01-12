package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.AdminRocketToWalletMapper;
import com.xinlian.biz.dao.TWalletInfoMapper;
import com.xinlian.biz.dao.TWalletTradeOrderMapper;
import com.xinlian.biz.model.AdminRocketToWallet;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * com.xinlian.admin.biz.service
 *
 * @author by Song
 * @date 2020/4/10 21:15
 */
@Service
@Slf4j
public class WalletInfoService extends PageBaseService<TWalletInfo> {

    @Autowired
    private TWalletInfoMapper walletInfoMapper;
    @Autowired
    private TWalletTradeOrderMapper walletTradeOrderMapper;

    @Autowired
    private TWalletTradeOrderMapper tWalletTradeOrderMapper;

    @Autowired
    private AdminRocketToWalletMapper adminRocketToWalletMapper;

    @Override
    public List<TWalletInfo> query(TWalletInfo model) throws Exception {
        return null;
    }

     //火箭封装参数所用
    private TWalletTradeOrder  setOrder (BigDecimal balance, Long uid,String coinName,String rocketPhone){
        TWalletTradeOrder order = new TWalletTradeOrder();
        if (coinName.equals("USDT")){
            order.setCurrencyId(5L);
        }
        if (coinName.equals("CAT")){
            order.setCurrencyId(6L);
        }
        if (coinName.equals("CAG")){
            order.setCurrencyId(211L);
        }
        order.setUid(uid);
        order.setTradeCurrencyNum(balance);
        order.setCurrencyCode(coinName);
        order.setDes("rocket转入");
        order.setTradeStatus(7);
        order.setCounterPartyMobile(rocketPhone);
        order.setTradeType(2);
        order.setTxId("1");
        order.setCreateTime(new Date());
        order.setTradeFee(BigDecimal.ZERO);
        order.setMinersFee(BigDecimal.ZERO);
        return order;
    }

    //火箭封装参数
    private AdminRocketToWallet setRecord(BigDecimal balance, Long catUid,String coinName,String rocketPhone,Integer status,Long rocketUid){
        AdminRocketToWallet rocketToWallet = new AdminRocketToWallet();
        rocketToWallet.setAmount(balance.longValue());
        rocketToWallet.setCatUid(catUid);
        rocketToWallet.setRocketPhone(rocketPhone);
        rocketToWallet.setStatus(status);
        rocketToWallet.setRocketUid(rocketUid);
        rocketToWallet.setCoinName(coinName);
        rocketToWallet.setCreatTime(new Date());
        return rocketToWallet;
    }


    //增加钱包余额，增加流水记录
    @Transactional
    public ResponseResult updateWalletMoney(BigDecimal balance, Long uid, String coinName, String rocketPhone,Long rocketUid) {
        ResponseResult result = new ResponseResult();
        TWalletTradeOrder tWalletTradeOrder = setOrder(balance, uid,coinName,rocketPhone);
        //查询钱包信息
        TWalletInfo tWalletInfo = walletInfoMapper.queryWalletByUid(uid,coinName);
        //找到钱包信息，把余额更新，插入流水记录
        if (null!=tWalletInfo){
            BigDecimal oldBalance = tWalletInfo.getBalanceNum();
            BigDecimal newBalance = oldBalance.add(balance);
            Integer updateWalletMoney = walletInfoMapper.updateWalletMoney(newBalance, uid,coinName);
            Integer addOrder = tWalletTradeOrderMapper.insert(tWalletTradeOrder);
            if (updateWalletMoney==1 && addOrder==1){
                AdminRocketToWallet adminRocketToWallet = setRecord(balance, uid, coinName, rocketPhone, 1, rocketUid);
                adminRocketToWalletMapper.insert(adminRocketToWallet);
                result.setMsg("钱包增加余额成功");
                result.setCode(200);
                return result;
            }else {
                AdminRocketToWallet adminRocketToWallet = setRecord(balance, uid, coinName, rocketPhone, 0, rocketUid);
                adminRocketToWalletMapper.insert(adminRocketToWallet);
                result.setMsg("钱包增加余额失败");
                result.setCode(400);
                return result;
            }
        }
        result.setCode(200);
        result.setMsg("未获取到钱包信息");
        return result;
    }



    @Transactional
    public void singleRechargeRequest(TWalletInfo walletInfo, TWalletTradeOrder walletTradeOrder) {
        //1.充值-更新钱包
        TWalletInfo getWalletInfo = walletInfoMapper.getByCriteria(walletInfo);
        getWalletInfo.setBalanceNum(UdunBigDecimalUtil.addNum(getWalletInfo.getBalanceNum(),walletInfo.getBalanceNum()));
        int resultNum = walletInfoMapper.updateModel(getWalletInfo);
        //2.插入记录
        walletTradeOrder.setTradeAddress(getWalletInfo.getCurrencyAddress());
        int saveNum = walletTradeOrderMapper.saveModel(walletTradeOrder);
        if(resultNum==0 || saveNum==0){
            throw new BizException("单个充值，保存数据出现异常!");
        }
    }

    @Transactional
    public void batchRecharge(Map<String, Object> convertParamMap) {
        List<TWalletInfo> walletInfos = (List<TWalletInfo>)convertParamMap.get("walletInfo");
        List<TWalletTradeOrder> tradeOrders = (List<TWalletTradeOrder>)convertParamMap.get("tradeOrder");
        int batchUpdateNum = walletInfoMapper.batchUpdateWalletInfo(walletInfos);
        int batchInsertNum = walletTradeOrderMapper.batchChainOwnerTradeRecord(tradeOrders);
        if((batchInsertNum==0||batchUpdateNum==0) && batchInsertNum != tradeOrders.size()){
            throw new BizException("批量充值，保存数据出现异常!");
        }
    }


}
