package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSON;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.malechain.vo.request.MaleChainRechargeCallBackRequest;
import com.xinlian.member.biz.malechain.vo.request.MaleChainWithdrawCallBackRequest;
import com.xinlian.member.biz.service.TWalletTradeOrderService;
import com.xinlian.member.biz.udun.UdunConstant;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 优盾回调处理类
 */
@Component
@Slf4j
public class MaleChainCallbackHandler {

    @Autowired
    private TWalletTradeOrderService walletTradeOrderService;

    /**
     * 处理MaleChain - 充值回调
     * @param data
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "maleChain充值回调接口")
    public String doRechargeCallbackMethod(String data,String rechargeOperType){
        try {
            log.info("收到充币成功回调[{}]", data);
            MaleChainRechargeCallBackRequest maleChainRechargeCallBackRequest = JSON.parseObject(data, MaleChainRechargeCallBackRequest.class);
            //充币回调
            return walletTradeOrderService.chargeMoneyCallbackHandle(maleChainRechargeCallBackRequest,rechargeOperType);
        }catch (BizException e){
            return e.getMsg();
        }
    }

    /**
     * 处理MaleChain - 提币回调
     * @param data
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "maleChain提币回调接口")
    public String doWithdrawCallbackMethod(String data) {
        MaleChainWithdrawCallBackRequest maleChainRechargeCallBackRequest = JSON.parseObject(data, MaleChainWithdrawCallBackRequest.class);
        log.info("收到提币回调[{}]", data);
        walletTradeOrderService.withdrawCashCallbackDispose(maleChainRechargeCallBackRequest);
        return UdunConstant.MAIL_CHAIN_OK;
    }





}
