package com.xinlian.member.biz.service;

import com.spark.bipay.constant.CoinType;
import com.spark.bipay.http.ResponseMessage;
import com.spark.bipay.http.client.BiPayClient;
import com.xinlian.biz.model.Address;
import com.xinlian.member.biz.udun.UdunConfig;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BiPayService {

    @Autowired
    private UdunConfig udunConfig;
    /**
     * 创建币种地址
     * @param coinType
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "生成优盾地址接口")
    public Address createCoinAddress(CoinType coinType){
        String callbackUrl = udunConfig.getCallbackRoot() + udunConfig.getCallbackUri();
        try {
            BiPayClient biPayClient = new BiPayClient(udunConfig.getGatewayHost(),udunConfig.getMerchantId(),udunConfig.getMerchantKey());
            ResponseMessage<com.spark.bipay.entity.Address> resp =  biPayClient.createCoinAddress(coinType.getCode(), callbackUrl,"","");
            Address address = new Address();
            address.setAddress(resp.getData().getAddress());
            address.setCoinType(resp.getData().getCoinType());
            return  address;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
