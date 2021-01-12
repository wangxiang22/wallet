package com.xinlian.admin.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jsms.api.SendSMSResult;
import cn.jsms.api.ValidSMSResult;
import cn.jsms.api.common.SMSClient;
import cn.jsms.api.common.model.SMSPayload;
import com.xinlian.admin.service.base.BaseServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Slf4j
public class JiGuangSmsClientTest extends BaseServiceTest {

    private static final String APP_KEY = "e71e75bda5e45540a6f5eb48";
    private static final String MASTER_SECRET = "a186507fc266299492f511b4";
//    protected static final String APP_KEY ="d4ee2375846bc30fa51334f5";
//    protected static final String MASTER_SECRET = "cfb11ca45888cdd6388483f5";
    private SMSClient client = null;
    @Before
    public void before() throws Exception {
        client = new SMSClient(MASTER_SECRET, APP_KEY);
    }

    @Test
    public void testSendSMSCode() {
        String smsCode = "213412";
        SMSPayload payload = SMSPayload.newBuilder()
                .setMobileNumber("15801525748")
                .setTempId(1)
                .addTempPara("code", smsCode)
                //.setSignId(1380)
                .build();

//        JsonObject json = new JsonObject();
//        json.addProperty("mobile", "15801525748");
//        json.addProperty("temp_id", 1);

        //System.err.println(json.toString());

        try {
            SendSMSResult res = client.sendTemplateSMS(payload);
            assertTrue(res.isResultOK());
            log.info(res.toString());


            //验证code
            SendSMSResult sendSMSResult = client.sendVoiceSMSCode(payload);

        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Message: " + e.getMessage());
        }

    }

    @Test
    public void validCode() {
        String code = "213412";
        try {
            ValidSMSResult res = client.sendValidSMSCode("1",code);
            assertTrue(res.isResultOK());
            log.info(res.toString());
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Message: " + e.getMessage());
        }

    }
}
