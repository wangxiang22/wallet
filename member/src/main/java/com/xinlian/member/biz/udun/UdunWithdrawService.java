package com.xinlian.member.biz.udun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spark.bipay.http.ResponseMessage;
import com.spark.bipay.http.client.BiPayClient;
import com.xinlian.biz.model.TWalletTradeOrder;
import com.xinlian.common.utils.UdunBigDecimalUtil;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import com.xinlian.member.biz.udun.vo.request.WithdrawRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UdunWithdrawService {

    @Autowired
    private UdunConfig udunConfig;


    /**
     * 请求udun提币接口
     * @param walletTradeOrder
     * @return
     */
    public boolean udunWithdraw(TWalletTradeOrder walletTradeOrder) {
        WithdrawRequest withdrawRequest = this.createWithdrawRequest(walletTradeOrder);
        try {
            ResponseMessage responseMessage = this.doUdunWithdraw(withdrawRequest);
            if (responseMessage.getCode() == 200) {
                return true;
            }else{
                /**
                 * 其他错误码 以及解释
                 * 4005非法参数传入的数组数据为空
                 * 4183到账地址异常
                 * 4034币种精度为空 保存提币申请时，查询币种的精度返回值为空
                 */
                log.warn("发送提币返回码{},返回消息{}", responseMessage.getCode(),responseMessage.getMessage());
            }
        } catch (Exception e) {
            log.error("发送提币请求异常：{}",e.getMessage(),e);
            return false;
        }
        return false;
    }

    @UdunLogAnnotation(udunOpeType = "后台审核通过请求提币")
    public ResponseMessage doUdunWithdraw(WithdrawRequest withdrawRequest)throws Exception{
        log.info("请求参数：{}", JSONObject.toJSONString(withdrawRequest));
        JSONObject param = (JSONObject) JSON.toJSON(withdrawRequest);
        List<JSONObject> params = new ArrayList<>();
        params.add(param);
        BiPayClient biPayClient = new BiPayClient(udunConfig.getGatewayHost(),udunConfig.getMerchantId(),udunConfig.getMerchantKey());
        ResponseMessage responseMessage = biPayClient.transfer(withdrawRequest.getBusinessId(),
                new BigDecimal(withdrawRequest.getAmount()),withdrawRequest.getMainCoinType(),
                withdrawRequest.getCoinType(),withdrawRequest.getAddress(),withdrawRequest.getCallUrl(),withdrawRequest.getMemo());
        return responseMessage;
    }

    /**
     * 创建请求u盾提币请求参数
     * @param walletTradeOrder db单子数据
     * @return
     */
    private WithdrawRequest createWithdrawRequest(TWalletTradeOrder walletTradeOrder){
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAddress(walletTradeOrder.getTradeAddress());
        //如果提币数量-减除手续费小于0，提示错误
        BigDecimal tradeCurrencyNum = UdunBigDecimalUtil.convertPlus(walletTradeOrder.getTradeCurrencyNum());
        BigDecimal withAmount = UdunBigDecimalUtil.subNumAndCheckIsZero(tradeCurrencyNum,walletTradeOrder.getTradeFee());
        withdrawRequest.setAmount(withAmount.toString());
        withdrawRequest.setCallUrl(udunConfig.getCallbackRoot()+udunConfig.getCallbackUri());
        withdrawRequest.setMerchantId(udunConfig.getMerchantId());
        withdrawRequest.setBusinessId(walletTradeOrder.getId().toString());
        withdrawRequest.setMainCoinType(udunConfig.getMainCoinType());
        withdrawRequest.setCoinType(udunConfig.getTokenContractAddress());
        return withdrawRequest;
    }
}
