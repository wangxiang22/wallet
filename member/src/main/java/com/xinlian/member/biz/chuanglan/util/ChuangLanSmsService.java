package com.xinlian.member.biz.chuanglan.util;

import com.alibaba.fastjson.JSON;
import com.xinlian.biz.utils.AdminOptionsUtil;
import com.xinlian.biz.utils.VendorSmsConfigUtil;
import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
import com.xinlian.common.enums.NationTypeEnum;
import com.xinlian.common.enums.SmsSenderChoiceEnum;
import com.xinlian.common.enums.VendorSmsConfigEnum;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.common.result.BizException;
import com.xinlian.common.utils.SystemUtils;
import com.xinlian.member.biz.chuanglan.SendSmsLogService;
import com.xinlian.member.biz.chuanglan.model.request.SmsSendRequest;
import com.xinlian.member.biz.chuanglan.model.response.SmsSendResponse;
import com.xinlian.member.biz.chuangrui.ChuangRuiSmsService;
import com.xinlian.member.biz.smsvendor.SendSmsSerivce;
import com.xinlian.member.biz.smsvendor.aliyun.AliyunSmsService;
import com.xinlian.member.biz.smsvendor.sendcloud.SendCloudSmsService;
import com.xinlian.member.biz.smsvendor.yunpian.YunPianSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



@Slf4j
@Service
public class ChuangLanSmsService {

    public static final String charset_UTF8 = "UTF-8";

    @Autowired
    private ChuangLanInlandSmsConfig chuangLanInlandSmsConfig;
    @Autowired
    private ChuangLanAbroadSmsConfig chuangLanAbroadSmsConfig;
    @Autowired
    private VendorSmsConfigUtil vendorSmsConfigUtil;
    @Autowired
    private AdminOptionsUtil adminOptionsUtil;
    @Value("${isTest}")
    private boolean isTest;
    @Autowired
    private ChuangRuiSmsService chuangRuiSmsService;
    @Autowired
    private AliyunSmsService aliyunSmsService;
    @Autowired
    private YunPianSmsService yunPianSmsService;
    @Autowired
    private SendCloudSmsService sendCloudSmsService;
    @Autowired
    private SendSmsLogService sendSmsLogService;
    @Autowired
    private SendSmsSerivce sendSmsSerivce;


    public String sendSmsByPost(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset",charset_UTF8);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
			//PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
			//printWriter.write(postContent);
			//printWriter.flush();
            httpURLConnection.connect();
            OutputStream os=httpURLConnection.getOutputStream();
            os.write(postContent.getBytes("UTF-8"));
            os.flush();
            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                // 开始获取数据
                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("创蓝发送短信异常:{}",e.getMessage(), e);
        }
        return null;
    }

    public boolean sendRegisterCodeCh(String phone, String code){
        return sendRegisterCodeCh(phone,code,null,null);
    }

    /**
     * 国内短信
     * @param phone
     * @param code
     * @param registerReq
     * @param smsRedisKey
     * @return
     */
    public boolean sendRegisterCodeCh(String phone, String code, RegisterReq registerReq,String smsRedisKey){
        String REGEX_MOBILE  = "^1\\d{10}$";
        if(!phone.matches(REGEX_MOBILE)){throw new BizException("请输入合法手机号码!");}
        if(isTest){return isTest;}
        String sendSmsType = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.SMS_SENDER_CODE.getBelongsSystemCode());
        //check 发送次数是否超限
        sendSmsSerivce.checkSendSmsNumberOfTimesByStr(sendSmsType,phone);
        if(SmsSenderChoiceEnum.SMS_CHUANGRUI.getCode().equals(sendSmsType)){
            return chuangRuiSmsService.sendSmsByChina(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_ALI_YUN.getCode().equals(sendSmsType)){
            return aliyunSmsService.sendSmsByChina(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_YUNPIAN.getCode().equals(sendSmsType)){
            return yunPianSmsService.sendSmsByChina(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_SEND_CLOUD.getCode().equals(sendSmsType)){
            return sendCloudSmsService.sendSmsByChina(phone,code,registerReq,smsRedisKey);
        }
        try {
            //拿新的--springbean容器初始化加载-就有
            chuangLanInlandSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_INLAND_SMS.getBelongsSystemCode(), ChuangLanInlandSmsConfig.class);
        }catch (Exception e){
            log.error("获取国内短信配置信息出现异常：{}",e.toString(),e);
            throw new BizException("获取国内短信配置信息出现异常!");
        }
        // 短信内容
        StringBuffer msgBuffer = new StringBuffer(chuangLanInlandSmsConfig.getInlandSmsSign());
        if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
            msgBuffer.append(chuangLanInlandSmsConfig.getInlandSmsRegisterTemplate());
        }else {
            msgBuffer.append(chuangLanInlandSmsConfig.getInlandSmsTemplate());
        }
        String msg = msgBuffer.toString().replace("@smsCode@",code);
        //状态报告
        String report= "true";
        SmsSendRequest smsSingleRequest =
                new SmsSendRequest(chuangLanInlandSmsConfig.getInlandAccount(), chuangLanInlandSmsConfig.getInlandPassword(), msg, phone, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        //System.out.println("before request string is: " + requestJson);
        String response = sendSmsByPost(chuangLanInlandSmsConfig.getInlandSmsUrl(), requestJson);
        //System.out.println("response after request result is :" + response);
        return this.analysisChuanglanResponse(phone,code,response, NationTypeEnum.INLAND.getDesc(),smsRedisKey);
    }

    private boolean analysisChuanglanResponse(String phone,String smsCode,String response,String nationType,String smsRedisKey){
        if(null==response){
            //返回null--进行
            return false;//chuangRuiSmsService.sendSmsByChina(phone,smsCode,registerReq);
        }
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);
        StringBuffer resultMsg = new StringBuffer();
        resultMsg.append(nationType).append("_");
        //获取内网ip
        if("客户端IP错误".equals(smsSingleResponse.getError())){
            String ip = SystemUtils.getV4IP();
            resultMsg.append("IP:"+ip);
        }
        if(null!= response && smsSingleResponse.getCode().equals("0")){
            resultMsg.append(smsSingleResponse.getMsgId()).append("_");
            resultMsg.append("推送短信到厂商成功");
            sendSmsLogService.saveSmsLog(phone,smsCode,resultMsg.toString(),"chuanglan",smsRedisKey);
            if(NationTypeEnum.INLAND.getDesc().equals(nationType)) {
                sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_CHUANGLAN,phone);
            }else{
                sendSmsSerivce.saveSendSmsNumberOfTimes(SmsSenderChoiceEnum.SMS_CHUANGLAN_ABROAD,phone);
            }
            return true;
        }
        resultMsg.append("错误消息：" + response);
        sendSmsLogService.saveSmsLog(phone,smsCode,resultMsg.toString(),"chuanglan",smsRedisKey);
        return false;
    }

    public boolean sendRegisterCodeInte(String phone, String code){
        return this.sendRegisterCodeInte(phone,code,null,null);
    }

    private static ThreadLocal<Integer> trySendInteNumber = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * 国际短信
     * @param phone
     * @param code
     * @param registerReq
     * @return
     */
    public boolean sendRegisterCodeInte(String phone, String code,RegisterReq registerReq,String smsRedisKey){
        if(isTest){return isTest;}
        String sendSmsType = adminOptionsUtil.findAdminOptionOne(AdminOptionsBelongsSystemCodeEnum.ABROAD_SMS_SENDER_CODE.getBelongsSystemCode());
        //check 发送次数是否超限
        sendSmsSerivce.checkSendSmsNumberOfTimesByStr(sendSmsType,phone);
        if(SmsSenderChoiceEnum.SMS_CHUANGRUI_ABROAD.getCode().equals(sendSmsType)){
            return chuangRuiSmsService.sendSmsByOtherArea(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_ALI_YUN_ABROAD.getCode().equals(sendSmsType)){
            return aliyunSmsService.sendSmsOther(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_YUNPIAN_ABROAD.getCode().equals(sendSmsType)){
            return yunPianSmsService.sendSmsByOther(phone,code,registerReq,smsRedisKey);
        }else if(SmsSenderChoiceEnum.SMS_SEND_CLOUD_ABROAD.getCode().equals(sendSmsType)){
            return sendCloudSmsService.sendSmsByChina(phone,code,registerReq,smsRedisKey);
        }
        try {
            //拿新的--springbean容器初始化加载-就有
            chuangLanAbroadSmsConfig =
                    vendorSmsConfigUtil.fieldEntityObject(VendorSmsConfigEnum.APP_ABROAD_SMS.getBelongsSystemCode(), ChuangLanAbroadSmsConfig.class);
        }catch (Exception e){
            log.error("获取国际短信配置信息出现异常：{}",e.toString(),e);
            throw new BizException("获取国际短信配置信息出现异常!");
        }
        // 短信内容
        StringBuffer msgBuffer = new StringBuffer(chuangLanAbroadSmsConfig.getAbroadSmsSign());
        if(null != registerReq && (1==registerReq.getType() || 0==registerReq.getType())){
            msgBuffer.append(chuangLanAbroadSmsConfig.getAbroadSmsRegisterTemplate());
        }else {
            msgBuffer.append(chuangLanAbroadSmsConfig.getAbroadSmsTemplate());
        }
        String msg = msgBuffer.toString().replace("@smsCode@",code);
        //状态报告
        //String report= "true";
        Map map = new HashMap();
        //API账号
        map.put("account", chuangLanAbroadSmsConfig.getAbroadAccount());
        //API密码
        map.put("password", chuangLanAbroadSmsConfig.getAbroadPassword());
        //短信内容
        map.put("msg", msg);
        map.put("mobile", phone);//手机号
        String requestJson = JSON.toJSONString(map);
        //System.out.println("before request string is: " + requestJson);
        return trySendIntSms(phone,code,requestJson,registerReq,smsRedisKey);
    }

    //增加尝试发送 - 次数
    private boolean trySendIntSms(String phone, String code,String requestJson,RegisterReq registerReq,String smsRedisKey){
        String response = sendSmsByPost(chuangLanAbroadSmsConfig.getAbroadSmsUrl(), requestJson);
        //System.out.println("response after request result is :" + response);
        boolean sendFlag = this.analysisChuanglanResponse(phone,code,response,NationTypeEnum.ABROAD.getDesc(),smsRedisKey);
        int trySendNumber = trySendInteNumber.get().intValue();
        while(null==response && trySendNumber < 5){
            response = sendSmsByPost(chuangLanAbroadSmsConfig.getAbroadSmsUrl(), requestJson);
            trySendNumber = trySendNumber + 1;
            sendFlag = this.analysisChuanglanResponse(phone,code,response,"尝试:["+trySendNumber+"]国际",smsRedisKey);
        }
        return sendFlag;
    }



}
