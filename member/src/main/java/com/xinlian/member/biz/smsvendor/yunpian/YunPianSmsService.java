package com.xinlian.member.biz.smsvendor.yunpian;

import com.xinlian.biz.utils.VendorSmsConfigUtil;
import com.xinlian.common.enums.NationTypeEnum;
import com.xinlian.common.enums.SmsSenderChoiceEnum;
import com.xinlian.common.enums.VendorSmsConfigEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.smsvendor.SendSmsSerivce;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author Song
 * @date 2020-07-11 11:48
 * @description 云片send sms service
 */
@Slf4j
@Service
public class YunPianSmsService {

    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private SendSmsSerivce sendSmsSerivce;

    private Result doSendSms(String apiKey,String phone,String sendText){
        //初始化clnt,使用单例方式
        YunpianClient clnt = new YunpianClient(apiKey).init();
        //发送短信API
        Map<String, String> param = clnt.newParam(2);
        param.put(YunpianClient.MOBILE, phone);
        param.put(YunpianClient.TEXT, sendText);
        Result<SmsSingleSend> r = clnt.sms().single_send(param);
        //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
        //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
        //释放clnt
        clnt.close();
        return r;
    }

    public boolean sendSmsByChina(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        YunPianInlandSmsConfig yunPianInlandSmsConfig = null;
        try {
            yunPianInlandSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_YUNPIAN_INLAND_SMS.getBelongsSystemCode(), YunPianInlandSmsConfig.class);
            // 短信内容
            String message = "";
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                message = yunPianInlandSmsConfig.getRegisterSmsTemplate();
            }else {
                message = yunPianInlandSmsConfig.getSmsTemplate();
            }
            String smsContent = message.replace("@smsCode@",code);
            Result result = this.doSendSms(yunPianInlandSmsConfig.getApiKey(),phone,smsContent);
            return this.analysisAliyunResponse(phone,code,result, NationTypeEnum.INLAND.getDesc(),smsRedisKey);
        } catch (BizException e) {
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "云片:发送国内短信出现异常!{}", e.toString(), e);
            return false;
        }
    }

    public boolean sendSmsByOther(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        YunPianAbroadSmsConfig yunPianAbroadSmsConfig = null;
        try {
            yunPianAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_YUNPIAN_ABROAD_SMS.getBelongsSystemCode(), YunPianAbroadSmsConfig.class);
            // 短信内容
            String message = "";
            phone = "+" + phone;
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                message = yunPianAbroadSmsConfig.getRegisterSmsTemplate();
            }else {
                message = yunPianAbroadSmsConfig.getSmsTemplate();
            }
            String smsContent = message.replace("@smsCode@",code);
            Result result = this.doSendSms(yunPianAbroadSmsConfig.getApiKey(),phone,smsContent);
            return this.analysisAliyunResponse(phone,code,result,NationTypeEnum.ABROAD.getDesc(),smsRedisKey);
        } catch (BizException e) {
            log.error("云片发送国际短信出现异常:{}",e.getMsg(),e);
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "云片:发送国际短信出现异常!{}", e.toString(), e);
            return false;
        }
    }

    private boolean analysisAliyunResponse(String phone, String code, Result result, String areaType, String smsRedisKey) {
        StringBuffer resultMsg = new StringBuffer();
        resultMsg.append(areaType).append("_");
        if(null!= result && 0==result.getCode()){
            resultMsg.append(result.getData() + "_");
            resultMsg.append("推送短信到厂商成功");
            sendSmsLogService.saveSmsLog(phone, code, resultMsg.toString(), "yunpian", smsRedisKey);
            //记录推送成功次数
            if(NationTypeEnum.INLAND.getDesc().equals(areaType)) {
                sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_YUNPIAN,phone);
            }else{
                sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_YUNPIAN_ABROAD,phone);
            }
            return true;
        }
        resultMsg.append("错误消息：" + result.getMsg());
        sendSmsLogService.saveSmsLog(phone,code,resultMsg.toString(),"yunpian",smsRedisKey);
        if(null!=result && 3==result.getCode()){
            throw new BizException("发送验证码过于频繁，请稍后重试!!");
        }
        if(null!=result && 0!=result.getCode()){
            throw new BizException("发送验证码过于频繁，请稍后重试。");
        }
        return false;
    }

    @Data
    class YunPianResultData{
        /**
         * code : 0
         * msg : 发送成功
         * count : 1
         * fee : 0.057
         * unit : RMB
         * mobile : +15046123985
         * sid : 56038520424
         */
        private int code;
        private String msg;
        private int count;
        private double fee;
        private String unit;
        private String mobile;
        private long sid;
    }
}
