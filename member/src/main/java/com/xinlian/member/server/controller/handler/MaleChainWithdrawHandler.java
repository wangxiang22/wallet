package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.TWalletInfo;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.enums.WalletTradeOrderStatusEnum;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.member.biz.malechain.MaleChainConfig;
import com.xinlian.member.biz.redis.RedisConstant;
import com.xinlian.member.biz.service.MaleChainService;
import com.xinlian.member.biz.service.TWalletInfoService;
import com.xinlian.member.biz.service.TWalletTradeOrderService;
import com.xinlian.member.biz.udun.CompleteScheduleConfig;
import com.xinlian.member.biz.udun.vo.request.MaleChainExtractRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

/**
 *
 */

@Component
@Slf4j
public class MaleChainWithdrawHandler extends CompleteScheduleConfig {

    @Autowired
    private TWalletTradeOrderService walletTradeOrderService;
    @Autowired
    private TWalletInfoService walletInfoService;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private MaleChainService maleChainService;
    @Autowired
    private MaleChainConfig maleChainConfig;

    @Override
    public String getCronMapper(){
        String cronRedisKey = RedisConstant.APP_REDIS_PREFIX + "CRON_UDUN_WITHDRAW";
        String redisValue = redisClient.get(cronRedisKey);
        if(null==redisValue){
            redisValue = cronMapper.getReqUdunCron();
            redisClient.set(cronRedisKey,redisValue);
            return redisValue;
        }else{
            return redisValue;
        }
    }
    private String redisLockKey = "REQ_UDUN_INTERFACE_REDIS_LOCK";
    /**
     *     提币-请求优盾接口
     */
    @Override
    public void doSubClassTask() {
        log.info("maleChain提币定时任务:doSubClassTask");
        Lock lock = redisLockRegistry.obtain(redisLockKey);
        boolean redisLockFlag = true;
        try{
            log.warn(Thread.currentThread().getName()+" : 提币请求开始;redisLockFlag:[{}]" , redisLockFlag);
            if(!lock.tryLock()){
                redisLockFlag = false;
                log.warn(Thread.currentThread().getName() + " : 请求提币接口获取分布式锁失败！;redisLockFlag:[{}]" , redisLockFlag);
                return ;
            }
            log.info(Thread.currentThread().getName() + " : 请求提币接口获取分布式锁成功！;redisLockFlag:[{}]" , redisLockFlag);
            //从库中获取已审核通过的
            TWalletTradeOrder walletTradeOrder = walletTradeOrderService.getAdminAuditPassTradeOrder();
            if(null==walletTradeOrder){
                log.debug("maleChain提币定时任务:doSubClassTask~~~~没有数据");
                return ;
            }
            //调用公链提币接口
            boolean flag = this.judgeMaleChainWithdrawRequest(walletTradeOrder);
            //记录转账订单
            if(flag){
                //更新钱包交易表为等待回调状态
                TWalletTradeOrder updateModel = new TWalletTradeOrder();
                updateModel.setId(walletTradeOrder.getId());
                updateModel.setTradeStatus(WalletTradeOrderStatusEnum.AUDIT_PASS.getCode());
                updateModel.setOldTradeStatus(WalletTradeOrderStatusEnum.ADMIN_PASS_PASS.getCode());
                walletTradeOrderService.updateWalletTradeOrder(updateModel);
                log.debug("更新钱包交易表为等待回调状态");
            }else{
                //解冻冻结金额 - 冻结金额不能全部解冻，而是传入值
                walletTradeOrderService.transactionalUnFreezeBiz(walletTradeOrder);
            }
        }finally {
            if(redisLockFlag) {
                redisLockFlag = false;
                lock.unlock();
                log.info(Thread.currentThread().getName()+" : 请求提币接口释放分布式锁success！;redisLockFlag:[{}]" , redisLockFlag);
            }else{
                log.info(Thread.currentThread().getName()+" : 请求提币接口分布式锁未释放;redisLockFlag:[{}]" , redisLockFlag);
            }
        }
    }

    public boolean judgeMaleChainWithdrawRequest(TWalletTradeOrder walletTradeOrder){
        try {
            MaleChainExtractRequest maleChainExtractRequest = this.createWithdrawRequest(walletTradeOrder);
            JSONObject responseObj = maleChainService.doMaleChainWithdraw(maleChainExtractRequest);
            if (200 == responseObj.getIntValue("code")) {
                //更新txid -- 解密data
                String encryptData = this.decryptByPrivateKey(responseObj.getString("data"));
                JSONObject txJson = JSONObject.parseObject(encryptData);
                String txId = txJson.getString("data");
                walletTradeOrder.setTxId(txId);
                int resultNum = walletTradeOrderService.updateWalletTradeOrder(walletTradeOrder);
                if(resultNum<1) {
                    log.error("交易订单号：[{}];未更新到txId：[{}]",walletTradeOrder.getId(),txId);
                }
                return true;
            }else{
                /**
                 * 其他错误码 以及解释
                 */
                log.warn("发送提币返回码{},返回消息{}", responseObj.getString("code"),responseObj.getString("message"));
            }
        } catch (Exception e) {
            log.error("发送提币异常：{}",e.getMessage(),e);
            return false;
        }
        return false;
    }

    public String decryptByPrivateKey(String encryptData)throws Exception{
        if(null==encryptData){
            log.error("maleChain提币接口 - 无data");
            throw new BizException("无data值");
        }
        byte [] decryptByte = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(encryptData),maleChainConfig.getPrivateKey());
        return new String(decryptByte);
    }

    private MaleChainExtractRequest createWithdrawRequest(TWalletTradeOrder walletTradeOrder){
        TWalletInfo whereWalletInfo = new TWalletInfo();
        whereWalletInfo.setUid(walletTradeOrder.getUid());
        whereWalletInfo.setCurrencyId(walletTradeOrder.getCurrencyId());
        TWalletInfo getWalletInfo = walletInfoService.getByCriteria(whereWalletInfo);
        if(null==getWalletInfo){throw new BizException("获取from地址错误");}
        MaleChainExtractRequest extractRequest = new MaleChainExtractRequest();
        extractRequest.setTo(walletTradeOrder.getTradeAddress());
        extractRequest.setFrom(getWalletInfo.getCurrencyAddress());
        BigDecimal tradeNumValuePlus = UdunBigDecimalUtil.convertPlus(walletTradeOrder.getTradeCurrencyNum());
        //提币值，需要减去系统费用
        BigDecimal withdrawValue = UdunBigDecimalUtil.subtractNum(tradeNumValuePlus,walletTradeOrder.getTradeFee());
        BigDecimal disposeValueDecimal = UdunBigDecimalUtil.multiplyValueDecimal(withdrawValue,UdunBigDecimalUtil.maleChainUSDT);
        extractRequest.setValue(disposeValueDecimal.longValue());
        extractRequest.setBusinessId(walletTradeOrder.getId());
        return extractRequest;
    }
}
