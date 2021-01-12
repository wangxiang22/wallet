package com.xinlian.member.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.Address;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.malechain.MaleChainConfig;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import com.xinlian.member.biz.udun.vo.request.MaleChainExtractRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MaleChainService {
    @Autowired
    private CallInterfaceService callInterfaceService;
    @Autowired
    private MaleChainConfig maleChainConfig;

    /**
     * 创建币种地址
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "生成maleChain地址接口")
    public Address createMaleChainAddress(int codeType){
        //String callbackUrl = maleChainConfig.getCallbackRoot() + maleChainConfig.getCallbackUri();
        try {
            String url = maleChainConfig.getGatewayHost()+maleChainConfig.getCreateAddress();
            JSONObject jsonObject = callInterfaceService.callInterface("",url,JSONObject.class);
            //检验--
            if(200!=jsonObject.getIntValue("code")){
                log.error("请求生成公链币地址返回异常:");
                throw new BizException("请求生成公链币地址失败!"+jsonObject);
            }
            //校验公私钥
            String data = jsonObject.getString("data");
            byte [] getDataByte = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(data),maleChainConfig.getPrivateKey());
            String addressObjectStr = new String(getDataByte);
            JSONObject addressJson = JSONObject.parseObject(addressObjectStr);
            Address address = new Address();
            address.setAddress(addressJson.getString("addr"));
            return address;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求maleChain提币接口
     * @param maleChainExtractRequest
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "提币走maleChain接口")
    public JSONObject doMaleChainWithdraw(MaleChainExtractRequest maleChainExtractRequest) throws Exception{
        log.info("maleChain提币请求参数：{}", JSONObject.toJSONString(maleChainExtractRequest));
        String url = maleChainConfig.getGatewayHost() + maleChainConfig.getWithdraw();
        JSONObject param = (JSONObject) JSON.toJSON(maleChainExtractRequest);
        JSONObject requestObj = new JSONObject();
        log.debug("加密之前的参数：{}",param.toJSONString());
        byte [] paramByte = RSAEncrypt.encryptByPublicKey(param.toJSONString().getBytes(),maleChainConfig.getPublicKey());
        requestObj.put("data", Base64Utils.encode(paramByte));
        JSONObject responseObj = callInterfaceService.callInterface(requestObj.toJSONString(),url,JSONObject.class);
        return responseObj;
    }

}
