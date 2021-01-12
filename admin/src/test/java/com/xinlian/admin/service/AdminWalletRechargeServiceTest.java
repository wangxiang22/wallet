package com.xinlian.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.service.WalletInfoService;
import com.xinlian.admin.service.base.BaseServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author Song
 * @date 2020-05-20 15:56
 * @description
 */
public class AdminWalletRechargeServiceTest extends BaseServiceTest {

    @Autowired
    private WalletInfoService walletInfoService;

    @Test
    public void batchRecharge(){
        String reqStr = "{\"rechargeTypeName\":\"222\",\"remark\":\"3322\",\"batchRechargeData\":\"491 USDT 2.2 \n" +
                "491 USDT 2.2\"}";
        JSONObject jsonObject = JSON.parseObject(reqStr);
        Map<String,Object> convertParamMap = null;//this.convertToParamMap(jsonObject);
        walletInfoService.batchRecharge(convertParamMap);
    }



}
