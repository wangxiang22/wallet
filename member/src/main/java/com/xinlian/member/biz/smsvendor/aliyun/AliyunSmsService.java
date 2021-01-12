package com.xinlian.member.biz.smsvendor.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.xinlian.biz.utils.VendorSmsConfigUtil;
import com.xinlian.common.enums.NationTypeEnum;
import com.xinlian.common.enums.SmsSenderChoiceEnum;
import com.xinlian.common.enums.VendorSmsConfigEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.DateFormatUtil;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.smsvendor.SendSmsSerivce;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * com.xinlian.member.biz.smsvendor.aliyun
 *
 * @author by Song
 * @date 2020/7/11 07:38
 */
@Slf4j
@Service
public class AliyunSmsService {

    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Autowired
    private SendSmsSerivce sendSmsSerivce;

    public CommonResponse doSendSms(DefaultProfile profile,CommonRequest request){
        IAcsClient client = new DefaultAcsClient(profile);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean sendSmsByChina(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        AliyunInlandSmsConfig aliyunInlandSmsConfig = null;
        try {
            aliyunInlandSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_ALIYUN_INLAND_SMS.getBelongsSystemCode(), AliyunInlandSmsConfig.class);
            CommonRequest request = new CommonRequest();
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", aliyunInlandSmsConfig.getInlandSignName());
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                request.putQueryParameter("TemplateCode", aliyunInlandSmsConfig.getInlandRegisterTemplate());
            }else {
                request.putQueryParameter("TemplateCode", aliyunInlandSmsConfig.getInlandTemplateCode());
            }
            //模板的参数值，key要和模板中一致，然后会将内容进行替换     为数组和SignNameJson通过下标对应
            request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
            DefaultProfile profile = DefaultProfile.getProfile("default", aliyunInlandSmsConfig.getInlandAccessKeyId(), aliyunInlandSmsConfig.getInlandAccessKeySecret());
            CommonResponse response = this.doSendSms(profile,request);
            log.info("请求发送短信接口结果：{}",response);
            return this.analysisAliyunResponse(phone,code,response, NationTypeEnum.INLAND.getDesc(),smsRedisKey);
        } catch (BizException e) {
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "阿里云:发送国内短信出现异常!{}", e.toString(), e);
            return false;
        }
    }


    public boolean sendSmsOther(String phone, String code, RegisterReq registerReq, String smsRedisKey) {
        AliyunAbroadSmsConfig aliyunAbroadSmsConfig = null;
        try {
            aliyunAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_ALIYUN_ABROAD_SMS.getBelongsSystemCode(), AliyunAbroadSmsConfig.class);
            CommonRequest request = new CommonRequest();
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", aliyunAbroadSmsConfig.getAbroadSmsSign());
            if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
                request.putQueryParameter("TemplateCode", aliyunAbroadSmsConfig.getAbroadRegisterTemplate());
            }else {
                request.putQueryParameter("TemplateCode", aliyunAbroadSmsConfig.getAbroadTemplateCode());
            }
            //模板的参数值，key要和模板中一致，然后会将内容进行替换     为数组和SignNameJson通过下标对应
            request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
            DefaultProfile profile = DefaultProfile.getProfile("default", aliyunAbroadSmsConfig.getAbroadAccessKeyId(), aliyunAbroadSmsConfig.getAbroadAccessKeySecret());
            CommonResponse response = this.doSendSms(profile,request);
            return this.analysisAliyunResponse(phone,code,response,NationTypeEnum.ABROAD.getDesc(),smsRedisKey);
        } catch (BizException e) {
            log.error("阿里云发送国际短信出现异常:{}",e.getMsg(),e);
            throw new BizException(e.getMsg());
        } catch (Exception e) {
            log.error(DateFormatUtil.get(7, new Date()) + "阿里云:发送国际短信出现异常!{}", e.toString(), e);
            return false;
        }
    }

    private boolean analysisAliyunResponse(String phone, String code, CommonResponse response, String areaType, String smsRedisKey) {
        StringBuffer resultMsg = new StringBuffer();
        resultMsg.append(areaType).append("_");
        String throwMessage = "";
        if(null!= response && 200==response.getHttpStatus()){
            AliyunSmsResult aliyunSmsResult = JSONObject.parseObject(response.getData(),AliyunSmsResult.class);
            if("OK".equals(aliyunSmsResult.getCode())) {
                resultMsg.append(aliyunSmsResult.getRequestId()+"_");
                resultMsg.append("推送短信到厂商成功");
                sendSmsLogService.saveSmsLog(phone, code, resultMsg.toString(), "aliyun",smsRedisKey);
                if(NationTypeEnum.INLAND.getDesc().equals(areaType)) {
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_ALI_YUN,phone);
                }else{
                    sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_ALI_YUN_ABROAD,phone);
                }
                return true;
            }else if("isv.DAY_LIMIT_CONTROL".equals(aliyunSmsResult.getCode())){ //aliyun超频错误码
                throwMessage = "发送验证码过于频繁，请稍后重试!";
            }else{
                throwMessage = aliyunSmsResult.getMessage();
            }
        }
        resultMsg.append("错误消息：" + response.getData());
        sendSmsLogService.saveSmsLog(phone,code,resultMsg.toString(),"aliyun",smsRedisKey);
        if(StringUtils.isNotEmpty(throwMessage)){
            throw new BizException(throwMessage);
        }
        return false;
    }
    
}
