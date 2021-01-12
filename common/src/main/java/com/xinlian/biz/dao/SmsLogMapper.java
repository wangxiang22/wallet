package com.xinlian.biz.dao;

import com.xinlian.biz.model.CheckErrorSmsModel;
import com.xinlian.biz.model.SmsLogModel;
import org.springframework.stereotype.Component;

/**
 * com.xinlian.biz.dao
 *
 * @date 2020/2/9 19:28
 */
@Component
public interface SmsLogMapper {

    /**
     * 保存sms请求记录
     * @param smsLogModel
     * @return
     */
    int threadSaveSmsLog(SmsLogModel smsLogModel);

    /**
     * 检验smscode报错记录到库
     * @param checkErrorSmsModel
     * @return
     */
    int threadCheckErrorSmsLog(CheckErrorSmsModel checkErrorSmsModel);
}
