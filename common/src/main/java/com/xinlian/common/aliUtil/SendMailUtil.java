package com.xinlian.common.aliUtil;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.xinlian.biz.model.AliYunEmailConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMailUtil {
    protected final static Logger logger = LoggerFactory.getLogger(SendMailUtil.class);

    public static boolean sample(String accessKeyId,String accessKeySecret,String accountName,String fromAlias,String tagName,
                                 String toAddress,String subject,String htmlBody) {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            //request.setVersion("2017-06-22");// 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName(accountName);//控制台创建的发信地址
            request.setFromAlias(fromAlias);//发信人昵称
            request.setAddressType(1);
            request.setTagName(tagName);//控制台创建的标签
            request.setReplyToAddress(true);
            request.setToAddress(toAddress);//目标地址
            request.setSubject(subject);//邮件主题
            //    如果采用byte[].toString的方式的话请确保最终转换成utf-8的格式再放入htmlbody和textbody，若编码不一致则会被当成垃圾邮件。
            //    注意：文本邮件的大小限制为3M，过大的文本会导致连接超时或413错误
            request.setHtmlBody(htmlBody);//邮件正文
            //  SDK 采用的是http协议的发信方式,默认是GET方法，有一定的长度限制。
            //  若textBody、htmlBody或content的大小不确定，建议采用POST方式提交，避免出现uri is not valid异常
            request.setMethod(MethodType.POST);
            // 开启需要备案，0关闭，1开启
            request.setClickTrace("0");
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);
            //捕获错误异常码
            System.out.println("ErrCode: " + e.getErrCode());
            e.printStackTrace();
            return false;
        } catch (ClientException e) {
            logger.error(e.getMessage(), e);
            //捕获错误异常码
            System.out.println("ErrCode: " + e.getErrCode());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean sample(AliYunEmailConfigModel configModel,String emailCode,String toAddress,String oper){
        String htmlBody = "CatWallet提醒您正在"+oper+"，验证码:"+emailCode+"，8分钟内有效，请勿泄露验证码给他人，保管好资产安全！";
        return sample(configModel.getAccessKeyId(), configModel.getAccessSecret(),configModel.getEmailAddress(),"官方:CatWallet邮件"
        ,"CAT2020",toAddress,"CatWallet邮件验证码",htmlBody);
    }

    public static boolean sampleLogin(AliYunEmailConfigModel configModel,String emailCode,String toAddress,String oper){
        String htmlBody = "CatWallet后台系统提醒您正在"+oper+"，验证码:"+emailCode+"，3分钟内有效，请勿泄露验证码给他人，保管好资产安全！";
        return sample(configModel.getAccessKeyId(), configModel.getAccessSecret(),configModel.getEmailAddress(),"官方:CatWallet邮件"
                ,"CAT2020",toAddress,"CatWallet邮件验证码:"+emailCode,htmlBody);
    }
}
