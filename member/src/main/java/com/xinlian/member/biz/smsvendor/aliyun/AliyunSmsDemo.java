package com.xinlian.member.biz.smsvendor.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;

/**
 * com.xinlian.member.biz.smsvendor.aliyun
 *
 * @author by Song
 * @date 2020/7/11 07:44
 */
public class AliyunSmsDemo {


    public static void main(String[] args) {
        DefaultProfile profile = DefaultProfile.getProfile("default", "LTAI4Fy8AwHBQQexWrpw6xbs", "VdOG2EhYcfe06baR2Xr3NKUlbiqlud");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", "17853186824");
        request.putQueryParameter("SignName", "Cat验证");
        request.putQueryParameter("TemplateCode", "SMS_195585811");
        //模板的参数值，key要和模板中一致，然后会将内容进行替换     为数组和SignNameJson通过下标对应
        request.putQueryParameter("TemplateParam", "{\"code\":\"251231\"}");
        //request.putQueryParameter("accessKeyId", "LTAI4Fy8AwHBQQexWrpw6xbs");
        //request.putQueryParameter("accessKeySecret", "VdOG2EhYcfe06baR2Xr3NKUlbiqlud");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }


}
