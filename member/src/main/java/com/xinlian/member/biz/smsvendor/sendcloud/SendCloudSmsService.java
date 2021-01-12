package com.xinlian.member.biz.smsvendor.sendcloud;

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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author Song
 * @date 2020-07-10 17:38
 * @description
 */
@Slf4j
@Service
public class SendCloudSmsService {
    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private SendSmsSerivce sendSmsSerivce;

    public String doSendSms(List<NameValuePair> postparams,String sendSmsReqUrl) throws ClientProtocolException, IOException{
        HttpPost httpPost = new HttpPost(sendSmsReqUrl);
        httpPost.setEntity(new UrlEncodedFormEntity(postparams, "utf8"));
        CloseableHttpClient httpClient;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).setSocketTimeout(100000).build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    private List<NameValuePair> packageRequestParam(SendCloudBaseSmsConfig sendCloudBaseSmsConfig, Map<String, String> params){
        // 对参数进行排序
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String arg0, String arg1) {
                // 忽略大小写
                return arg0.compareToIgnoreCase(arg1);
            }
        });
        sortedMap.putAll(params);
        // 计算签名
        StringBuilder sb = new StringBuilder();
        sb.append(sendCloudBaseSmsConfig.getSmsKey()).append("&");
        for (String s : sortedMap.keySet()) {
            sb.append(String.format("%s=%s&", s, sortedMap.get(s)));
        }
        sb.append(sendCloudBaseSmsConfig.getSmsKey());
        String sig = DigestUtils.md5Hex(sb.toString());

        // 将所有参数和签名添加到post请求参数数组里
        List<NameValuePair> postparams = new ArrayList<NameValuePair>();
        for (String s : sortedMap.keySet()) {
            postparams.add(new BasicNameValuePair(s, sortedMap.get(s)));
        }
        postparams.add(new BasicNameValuePair("signature", sig));
        return postparams;
    }

    public boolean sendSmsByChina(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        SendCloudInlandSmsConfig sendCloudSmsConfig = null;
        try {
            sendCloudSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_SEND_CLOUD_INLAND_SMS.getBelongsSystemCode(), SendCloudInlandSmsConfig.class);
            // 填充参数
            Map<String, String> params = new HashMap<String, String>();
            params.put("smsUser", sendCloudSmsConfig.getSmsUser());
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                params.put("templateId", sendCloudSmsConfig.getRegisterTemplateIdStr());
            }else {
                params.put("templateId", sendCloudSmsConfig.getTemplateIdStr());
            }
            params.put("msgType", sendCloudSmsConfig.getMsgType());
            params.put("phone", phone);
            params.put("vars", "{\"code\":\""+code+"\"}");
            List<NameValuePair> postparams = this.packageRequestParam(sendCloudSmsConfig,params);
            String result = this.doSendSms(postparams,sendCloudSmsConfig.getRequestSmsUrl());
            log.info("请求发送短信接口结果：{}",result);
            return this.analysisSendCloudResponse(phone,code,result, NationTypeEnum.INLAND.getDesc(),smsRedisKey);
        } catch (BizException e) {
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "SendCloud:发送国内短信出现异常!{}", e.toString(), e);
            return false;
        }
    }


    public boolean sendSmsOther(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        SendCloudAbroadSmsConfig sendCloudAbroadSmsConfig = null;
        try {
            sendCloudAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_SEND_CLOUD_ABROAD_SMS.getBelongsSystemCode(), SendCloudAbroadSmsConfig.class);
            sendCloudAbroadSmsConfig.setMsgType("2");//代表国际短信
            // 填充参数
            Map<String, String> params = new HashMap<String, String>();
            phone = "+" + phone;
            params.put("smsUser", sendCloudAbroadSmsConfig.getSmsUser());
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                params.put("templateId", sendCloudAbroadSmsConfig.getRegisterTemplateIdStr());
            }else {
                params.put("templateId", sendCloudAbroadSmsConfig.getTemplateIdStr());
            }
            params.put("msgType", sendCloudAbroadSmsConfig.getMsgType());
            params.put("phone", phone);
            params.put("vars", "{\"code\":\""+code+"\"}");
            List<NameValuePair> postparams = this.packageRequestParam(sendCloudAbroadSmsConfig,params);
            String result = this.doSendSms(postparams,sendCloudAbroadSmsConfig.getRequestSmsUrl());
            return this.analysisSendCloudResponse(phone,code,result,NationTypeEnum.ABROAD.getDesc(),smsRedisKey);
        } catch (BizException e) {
            log.error("SendCloud发送国际短信出现异常:{}",e.getMsg(),e);
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "SendCloud:发送国际短信出现异常!{}", e.toString(), e);
            return false;
        }
    }

    private boolean analysisSendCloudResponse(String phone, String code, String requestResult, String areaType, String smsRedisKey) {
        SendCloudResult sendCloudResult = JSONObject.parseObject(requestResult,SendCloudResult.class);
        StringBuffer resultMsg = new StringBuffer();
        resultMsg.append(areaType).append("_");
        String throwMessage = "";
        if(null!= sendCloudResult && sendCloudResult.isResult()){
            if(200 == sendCloudResult.getStatusCode()) {
                resultMsg.append(sendCloudResult.getInfo().getSmsIds().get(0)+"_");
                resultMsg.append("推送短信到厂商成功");
                sendSmsLogService.saveSmsLog(phone, code, resultMsg.toString(), "sendCloud",smsRedisKey);
                if(NationTypeEnum.INLAND.getDesc().equals(areaType)) {
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_SEND_CLOUD,phone);
                }else{
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_SEND_CLOUD_ABROAD,phone);
                }
                return true;
            }
        }
        resultMsg.append("错误消息：" + sendCloudResult.getMessage());
        sendSmsLogService.saveSmsLog(phone,code,resultMsg.toString(),"sendCloud",smsRedisKey);
        if(StringUtils.isNotEmpty(throwMessage)){
            throw new BizException(throwMessage);
        }
        return false;
    }



}
