package com.xinlian.admin.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.redis.RedisConstant;
import com.xinlian.biz.utils.CommonRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;

/**
* 通用物体和场景识别
*/
@Service
public class BaiduAdvancedGeneral {
    @Autowired
    private CommonRedisClient commonRedisClient;

    public String advancedGeneral(String filePath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {
            // 本地文件路径
//            String filePath = "http://cat-web.oss-cn-hongkong.aliyuncs.com/idcard/d4044b68-2d29-4914-9a10-195bdc66e131-1597720311583-android-192.168.0.100.jpg";
            byte[] imgData = FileUtil.doGetRequestForFile(filePath);
            if (null == imgData) {
                return null;
            }
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = commonRedisClient.get(RedisConstant.BAIDU_ACCESS_TOKEN);
            if (null == accessToken) {
                accessToken = BaiduAuthService.getAuth();
                //获取access_token时也有可能是null
                if (null == accessToken) {
                    return null;
                }
                //Access Token的有效期(秒为单位，一般为1个月)
                commonRedisClient.set(RedisConstant.BAIDU_ACCESS_TOKEN,accessToken,2592000L);
            }
//            System.out.println(HttpUtil.post(url, accessToken, param));
            return HttpUtil.post(url, accessToken, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}