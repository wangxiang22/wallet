package com.xinlian.member.biz.smsvendor;

import com.xinlian.member.biz.smsvendor.base.SendSmsModel;

/**
 * @author Song
 * @date 2020-07-08 11:41
 * @description SMS厂商服务
 */
public interface SmsVendorService {



    /**
     * 发送除国内短信
     * @param sendSmsModel
     * @return
     */
    boolean doSendSmsOther(SendSmsModel sendSmsModel);

    /**
     * 发送国内短信
     * @param sendSmsModel
     * @return
     */
    boolean doSendSmsChina(SendSmsModel sendSmsModel);

}
