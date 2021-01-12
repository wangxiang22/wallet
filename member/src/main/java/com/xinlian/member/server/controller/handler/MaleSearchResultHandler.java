package com.xinlian.member.server.controller.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.malechain.MaleChainConfig;
import com.xinlian.member.biz.service.CallInterfaceService;
import com.xinlian.member.biz.udun.vo.request.MaleChainSearchResultRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 公链结果回调一直未推送，进行补偿处理
 * @author Song
 * @date 2020-05-16 10:50
 * @description
 */
@Component
@Slf4j
public class MaleSearchResultHandler {

    @Autowired
    private MaleChainConfig maleChainConfig;
    @Autowired
    private CallInterfaceService callInterfaceService;


    public String searchResultByTxHash(JSONObject jsonObject)throws Exception {
        MaleChainSearchResultRequest maleChainSearchResultRequest = this.convertToSearchRequestRequest(jsonObject);
        JSONObject responseObj = this.doRequestUrl(maleChainSearchResultRequest);
        return responseObj.toJSONString();
    }

    private MaleChainSearchResultRequest convertToSearchRequestRequest(JSONObject jsonObject) {
        MaleChainSearchResultRequest request = new MaleChainSearchResultRequest();
        request.setType(jsonObject.getString("type"));
        request.setTx_hash(jsonObject.getString("tx_hash"));
        return request;
    }

    public JSONObject doRequestUrl(MaleChainSearchResultRequest searchResultRequest) throws Exception{
        JSONObject requestObj = new JSONObject();
        String toUrl = maleChainConfig.getGatewayHost() + maleChainConfig.getSearchResultUri();
        JSONObject param = (JSONObject) JSON.toJSON(searchResultRequest);
        log.debug("maleChain结果补偿请求参数：{}",param.toJSONString());
        byte [] requestParamByte = RSAEncrypt.encryptByPublicKey(param.toJSONString().getBytes(),maleChainConfig.getPublicKey());
        requestObj.put("data", Base64Utils.encode(requestParamByte));
        JSONObject responseObj = callInterfaceService.callInterface(requestObj.toJSONString(),toUrl,JSONObject.class);
        return responseObj;
    }

    public String haveBalanceAddress() {
        try {
            String toUrl = maleChainConfig.getGatewayHost() + maleChainConfig.getHaveBalanceAddress();
            JSONObject jsonObject = callInterfaceService.callInterface("",toUrl,JSONObject.class);
            return jsonObject.toJSONString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
