package com.xinlian.member.biz.scheduling;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.TBlockmallPayCallbackErrorLog;
import com.xinlian.common.enums.CallbackTimeIntervalEnum;
import com.xinlian.common.redis.RedisKeys;
import com.xinlian.common.response.CatWalletPayCallbackErrorRes;
import com.xinlian.common.response.CatWalletPayCallbackRes;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.TBlockmallPayCallbackErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Set;


@Slf4j
@Configuration
@EnableScheduling
@Component
public class BlockmallCallbackTask {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private TBlockmallPayCallbackErrorLogService tBlockmallPayCallbackErrorLogService;

    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "0 */5 * * * ?")
    public void repeatPayCallbackError() {
        log.info(":::::::: start ::::::::");
        Set<String> keys = redisClient.getKeys(RedisKeys.repeatPayCallbackErrorKey("*"));
        if (null != keys) {
            log.info(":::::::: keys.size() ::::::::" + keys.size());
            Integer i = 0;
            for (String key : keys) {
                String strInRedis = redisClient.get(key);
                if (StringUtils.isNotBlank(strInRedis)) {
                    try {
                        CatWalletPayCallbackErrorRes resInRedis = JSONObject.parseObject(strInRedis, CatWalletPayCallbackErrorRes.class);
                        if (send(key, resInRedis)) {
                            i = i + 1;
                        }
                    } catch (Exception e) {
                        redisClient.deleteByKey(key);//解析异常，直接删除
                    }
                }
                if (i.compareTo(180) > 0) {
                    log.info(":::::::: 五分钟内完成180次回调请求，剩余的下一轮处理 ::::::::");
                    break;
                }
            }
        }
        log.info(":::::::: end ::::::::");
    }

    private boolean send(String key, CatWalletPayCallbackErrorRes resInRedis) {
        if (resInRedis.getCallbackTime().compareTo(System.currentTimeMillis()) < 0) {
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");

        HttpEntity<CatWalletPayCallbackRes> entity = new HttpEntity<CatWalletPayCallbackRes>(resInRedis.getCatWalletPayCallbackRes(), headers);

        try {
            log.info("::::支付结果回调第 " + resInRedis.getRepeateNum() + " 次重试，回调地址：" + resInRedis.getCallback()
                    + ", orderNo：" + resInRedis.getOrderNo());
            String str = restTemplate.postForObject(resInRedis.getCallback(), entity, String.class);
            asyncSaveLog(resInRedis, str);
            if (StringUtils.isNotBlank(str)) {
                Integer code = JSONObject.parseObject(str).getInteger("code");
                if (code.equals(200)) {//成功或业务异常
                    redisClient.deleteByKey(key);// 删除重试redis
                    return true;
                } else if (code.equals(501)) {
                    redisClient.deleteByKey(RedisKeys.blockmallOrderKey(resInRedis.getOrderNo()));
                    redisClient.deleteByKey(RedisKeys.blockmallOrderDetailKey(resInRedis.getOrderNo()));
                    redisClient.deleteByKey(key);// 删除重试redis
                    redisClient.set(RedisKeys.blockmallUnPayOrderKey(resInRedis.getOrderNo()), resInRedis.getOrderNo());
                    return true;
                }
            }
        } catch (Exception e) {
            asyncSaveLog(resInRedis, e.getMessage());
            log.info("::::支付结果回调第 " + resInRedis.getRepeateNum() + " 次重试异常，回调地址：" + resInRedis.getCallback()
                    + ", orderNo：" + resInRedis.getOrderNo() + ", 异常信息： " + e.getMessage());
        }

        // 一般情况下，25小时以内完成8次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
        if (resInRedis.getRepeateNum().compareTo(7) >= 0) {
            redisClient.deleteByKey(key);
        } else {
            // 重置redis
            Integer repeateNum = resInRedis.getRepeateNum() + 1;
            resInRedis.setRepeateNum(repeateNum);
            resInRedis.setCallbackTime(resInRedis.getCallbackTime() + CallbackTimeIntervalEnum.getTimeByCount(repeateNum));
            redisClient.set(key, JSONObject.toJSONString(resInRedis));
        }
        return true;
    }

    private void asyncSaveLog(CatWalletPayCallbackErrorRes resInRedis, String errMsg) {
        TBlockmallPayCallbackErrorLog tblog = new TBlockmallPayCallbackErrorLog();
        tblog.setRepeateNum(resInRedis.getRepeateNum());
        tblog.setCallback(resInRedis.getCallback());
        tblog.setCallbackTime(new Date());
        tblog.setOrderNo(resInRedis.getOrderNo());
        tblog.setErrMsg(errMsg);
        tblog.setData(resInRedis.getCatWalletPayCallbackRes().getData());
        tblog.setSign(resInRedis.getCatWalletPayCallbackRes().getSign());
        try {
            tBlockmallPayCallbackErrorLogService.insert(tblog);
        } catch (Exception e) {
        }
    }

}
