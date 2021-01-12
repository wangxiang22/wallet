package com.xinlian.member.biz.chuangrui;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.utils.VendorSmsConfigUtil;
import com.xinlian.common.enums.NationTypeEnum;
import com.xinlian.common.enums.SmsSenderChoiceEnum;
import com.xinlian.common.enums.VendorSmsConfigEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.smsvendor.SendSmsSerivce;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


@Slf4j
@Service
public class ChuangRuiSmsService {

    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private SendSmsSerivce sendSmsSerivce;

    private ChuangRuiSmsSendResponse sendsms(NameValuePair[] data, String reqUrl)  {
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod postMethod = new PostMethod(reqUrl);
            postMethod.getParams().setContentCharset("UTF-8");
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            postMethod.setRequestBody(data);
            postMethod.setRequestHeader("Connection", "close");
            int statusCode = httpClient.executeMethod(postMethod);
            ChuangRuiSmsSendResponse response = new ChuangRuiSmsSendResponse();
            response.setStatusCode(statusCode);
            response.setBody(postMethod.getResponseBodyAsString());
            return response;
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"调用创瑞发送短信接口出现异常,{}",e.toString(),e);
            return null;
        }
    }

    /**
     * 发送国内短信
     * @param phone
     * @param code
     * @param registerReq
     * @return
     */
    public boolean sendSmsByChina(String phone, String code, RegisterReq registerReq,String smsRedisKey){
        ChuangRuiInlandSmsConfig chuangRuiInlandSmsConfig = null;
        try {
            chuangRuiInlandSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_CHUANGRUI_INLAND_SMS.getBelongsSystemCode(), ChuangRuiInlandSmsConfig.class);
            // 短信内容
            StringBuffer smsTemplateId = new StringBuffer();
            StringBuffer smsContent = new StringBuffer();
            if (null != registerReq && (1 == registerReq.getType() || 0 == registerReq.getType())) {
                int indexOf = chuangRuiInlandSmsConfig.getInlandSmsRegisterTemplate().indexOf("@,@");
                smsTemplateId.append(chuangRuiInlandSmsConfig.getInlandSmsRegisterTemplate().substring(0, indexOf));
                smsContent.append(chuangRuiInlandSmsConfig.getInlandSmsRegisterTemplate().substring(indexOf + 3));
            } else {
                int indexOf = chuangRuiInlandSmsConfig.getInlandSmsRegisterTemplate().indexOf("@,@");
                smsTemplateId.append(chuangRuiInlandSmsConfig.getInlandSmsTemplate().substring(0, indexOf));
                smsContent.append(chuangRuiInlandSmsConfig.getInlandSmsTemplate().substring(indexOf + 3));
            }
            String smsMsg = smsContent.toString().replace("@smsCode@", code);
            NameValuePair[] data = {
                    new NameValuePair("accesskey", chuangRuiInlandSmsConfig.getAccesskey()),
                    new NameValuePair("secret", chuangRuiInlandSmsConfig.getAccessSecret()),
                    new NameValuePair("sign", chuangRuiInlandSmsConfig.getInlandSmsSign()),
                    new NameValuePair("templateId", smsTemplateId.toString()),
                    new NameValuePair("mobile", phone),
                    new NameValuePair("content", code)
            };
            ChuangRuiSmsSendResponse response = this.sendsms(data, chuangRuiInlandSmsConfig.getInlandSmsUrl());
            if (null != response) {

            }
            return this.analysisChuangRuiResponse(phone, code, response, NationTypeEnum.INLAND.getDesc(), smsRedisKey);
        }catch (BizException e){
            throw new BizException(e.getMsg());
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"创瑞发送国内短信出现异常!{}",e.toString(),e);
            return false;
        }
    }


    private boolean analysisChuangRuiResponse(String phone, String code, ChuangRuiSmsSendResponse response, String areaType,String smsRedisKey) {
        StringBuffer resultMsg = new StringBuffer();
        resultMsg.append(areaType).append("_");
        String throwMessage = "";
        if(null!= response && 200==response.getStatusCode()){
            ChuangRuiResult chuangRuiResult = JSONObject.parseObject(response.getBody(),ChuangRuiResult.class);
            if("0".equals(chuangRuiResult.getCode())) {
                resultMsg.append(chuangRuiResult.getSmUuid()+"_");
                resultMsg.append("推送短信到厂商成功");
                sendSmsLogService.saveSmsLog(phone, code, resultMsg.toString(), "chuangrui",smsRedisKey);
                if(NationTypeEnum.INLAND.getDesc().equals(areaType)) {
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_CHUANGRUI, phone);
                }else{
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_CHUANGRUI_ABROAD, phone);
                }
                return true;
            }else if("9024".equals(chuangRuiResult.getCode())){ //创瑞超频错误码
                throwMessage = "发送验证码过于频繁，请稍后重试!";
            }else{
                throwMessage = chuangRuiResult.getMsg();
            }
        }
        resultMsg.append("错误消息：" + response.getBody());
        sendSmsLogService.saveSmsLog(phone,code,resultMsg.toString(),"chuangrui",smsRedisKey);
        if(StringUtils.isNotEmpty(throwMessage)){
            throw new BizException(throwMessage);
        }
        return false;
    }

    /**
     * 其他区域发送短信
     * @param phone
     * @param code
     * @param registerReq
     * @param smsRedisKey
     * @return
     */
    public boolean sendSmsByOtherArea(String phone, String code, RegisterReq registerReq,String smsRedisKey){
        ChuangRuiAbroadSmsConfig chuangRuiAbroadSmsConfig = null;
        try {
            chuangRuiAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_CHUANGRUI_ABROAD_SMS.getBelongsSystemCode(), ChuangRuiAbroadSmsConfig.class);
            // 短信内容
            StringBuffer smsTemplateId = new StringBuffer();
            if (null != registerReq && (1 == registerReq.getType() || 0 == registerReq.getType())) {
                smsTemplateId.append(chuangRuiAbroadSmsConfig.getAbroadSmsRegisterTemplate());
            } else {
                smsTemplateId.append(chuangRuiAbroadSmsConfig.getAbroadSmsTemplate());
            }
            NameValuePair[] data = {
                    new NameValuePair("accesskey", chuangRuiAbroadSmsConfig.getAbroadAccesskey()),
                    new NameValuePair("secret", chuangRuiAbroadSmsConfig.getAbroadAccessSecret()),
                    new NameValuePair("sign", chuangRuiAbroadSmsConfig.getAbroadSmsSign()),
                    new NameValuePair("templateId", smsTemplateId.toString()),
                    new NameValuePair("mobile", phone),
                    new NameValuePair("content", code)
            };
            ChuangRuiSmsSendResponse response = this.sendsms(data, chuangRuiAbroadSmsConfig.getAbroadSmsUrl());
            return this.analysisChuangRuiResponse(phone, code, response, NationTypeEnum.ABROAD.getDesc(), smsRedisKey);
        }catch (BizException e){
            log.error("ChuangRui发送国际短信出现异常:{}",e.getMsg(),e);
            throw new BizException(e.getMsg());
        }catch (Exception e){
            log.error(DateFormatUtil.get(7,new Date())+"创瑞发送国际短信出现异常!{}",e.toString(),e);
            return false;
        }
    }

}
