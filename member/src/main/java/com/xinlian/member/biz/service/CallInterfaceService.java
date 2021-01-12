package com.xinlian.member.biz.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * @author by Song
 * @date 2018/8/24 16:40
 */
@Slf4j
@Service
public class CallInterfaceService {

    @Autowired
    private RestTemplate restTemplate;

    public <T> T callInterface(String jsonString, String url, Class<T> responseClass) throws Exception{
        T response = null;
        try {
            Assert.notNull(url,"接口地址不能为空");
            log.info("调用链接----------------：" + url);
            log.info("发送报文----------------：" + jsonString);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity formEntity = new HttpEntity(jsonString, headers);
            String resData = restTemplate.postForObject(url, formEntity, String.class);
            // 请求结束，返回结果
            log.info("调用接口响应报文：---------------- " + resData);
            response = (T) JSON.parseObject(resData, responseClass);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new Exception("请求接口超时");
        }
        return response;
    }
}
