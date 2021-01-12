package com.xinlian.member.biz.chuanglan;

import com.xinlian.biz.dao.SmsLogMapper;
import com.xinlian.biz.model.CheckErrorSmsModel;
import com.xinlian.biz.model.SmsLogModel;
import com.xinlian.common.request.RegisterReq;
import com.xinlian.member.biz.jwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class SendSmsLogService {

    @Autowired
    private SmsLogMapper smsLogMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private JwtUtil jwtUtil;

    public void saveSmsLog(String phone,String smsCode,String sendResult,String sender,String smsRedisKey){
        SmsLogModel smsLogModel = new SmsLogModel();
        smsLogModel.setAcceptorPhone(phone);
        smsLogModel.setSender(sender);//默认创蓝
        smsLogModel.setSmsCode(smsCode);
        smsLogModel.setSendResult(sendResult);
        smsLogModel.setSmsRedisKey(smsRedisKey);
        smsLogModel.setUid(this.getUidByRequest(httpServletRequest));
        new Thread(()->{
            smsLogMapper.threadSaveSmsLog(smsLogModel);
        }).start();
    }

    private Long getUidByRequest(HttpServletRequest httpServletRequest){
        try{
            return jwtUtil.getUserId(httpServletRequest);
        }catch (Exception e){
            return -1L;
        }
    }

    /**
     * 保存检验有误的短信内容
     * @param redisPhoneKey
     * @param redisCode
     * @param phone
     * @param req
     */
    @Async
    public void saveCheckErrorSmsCode(String redisPhoneKey, String redisCode, String phone, RegisterReq req) {
        CheckErrorSmsModel checkErrorSmsModel = new CheckErrorSmsModel();
        checkErrorSmsModel.setRedisSmsCode(redisCode);
        checkErrorSmsModel.setReqCode(req.getCode());
        checkErrorSmsModel.setSmsRedisKey(redisPhoneKey);
        checkErrorSmsModel.setPhone(phone);
        checkErrorSmsModel.setCountryCode(req.getCountryCode());
        smsLogMapper.threadCheckErrorSmsLog(checkErrorSmsModel);
    }
}
