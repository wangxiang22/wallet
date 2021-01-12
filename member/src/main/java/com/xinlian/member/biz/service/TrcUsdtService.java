package com.xinlian.member.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.Address;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.Base64Utils;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.common.utils.RSAEncrypt;
import com.xinlian.member.biz.trcusdt.TrcUsdtConfig;
import com.xinlian.member.biz.udun.aoplog.UdunLogAnnotation;
import com.xinlian.member.biz.udun.vo.request.MaleChainExtractRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TrcUsdtService {
    @Autowired
    private CallInterfaceService callInterfaceService;
    @Autowired
    private TrcUsdtConfig trcUsdtConfig;

    /**
     * 创建币种地址
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "生成TRC_USDT地址接口")
    public Address createTrcUsdtAddress(String coinType){
        try {
            String url = trcUsdtConfig.getGatewayHost()+trcUsdtConfig.getCreateAddress();
            JSONObject jsonObject = callInterfaceService.callInterface("",url,JSONObject.class);
            //检验--
            if(200!=jsonObject.getIntValue("code")){
                log.error("请求生成T【RC_USDT】公链币地址返回异常:");
                throw new BizException(DateFormatUtil.getByNowTime(7)+"请求生成【RC_USDT】公链币地址失败!"+jsonObject);
            }
            //校验公私钥
            String data = jsonObject.getString("data");
            byte [] getDataByte = RSAEncrypt.decryptByPrivateKey(Base64Utils.decode(data),trcUsdtConfig.getPrivateKey());
            String responseAddress = new String(getDataByte);
            Address address = new Address();
            address.setAddress(responseAddress);
            return address;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 请求TRC_USDT提币接口
     * @param maleChainExtractRequest
     * @return
     */
    @UdunLogAnnotation(udunOpeType = "提币走Trc_USDT接口")
    public JSONObject doTrcUsdtWithdraw(MaleChainExtractRequest maleChainExtractRequest) throws Exception{
        log.info("trc_usdt提币请求参数：{}", JSONObject.toJSONString(maleChainExtractRequest));
        String url = "";//trcUsdtConfig.getGatewayHost() + trcUsdtConfig.getWithdraw();
        JSONObject param = (JSONObject) JSON.toJSON(maleChainExtractRequest);
        JSONObject requestObj = new JSONObject();
        log.debug("加密之前的参数：{}",param.toJSONString());
        byte [] paramByte = RSAEncrypt.encryptByPrivateKey(param.toJSONString().getBytes(),trcUsdtConfig.getPrivateKey());
        requestObj.put("data", Base64Utils.encode(paramByte));
        JSONObject responseObj = callInterfaceService.callInterface(requestObj.toJSONString(),url,JSONObject.class);
        return responseObj;
    }

}
