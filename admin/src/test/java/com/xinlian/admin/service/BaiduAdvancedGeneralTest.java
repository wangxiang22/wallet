package com.xinlian.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xinlian.admin.biz.utils.BaiduAdvancedGeneral;
import com.xinlian.admin.service.base.BaseServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lt
 * @date 2020/08/18
 **/
public class BaiduAdvancedGeneralTest extends BaseServiceTest {
    @Autowired
    private BaiduAdvancedGeneral baiduAdvancedGeneral;

    @Test
    public void testBaidu() {
        long start = System.currentTimeMillis();
        String advancedGeneral = baiduAdvancedGeneral.advancedGeneral("http://cat-web.oss-cn-hongkong.aliyuncs.com/idcard/d4044b68-2d29-4914-9a10-195bdc66e131-1597720311583-android-192.168.0.100.jpg");
        JSONObject jsonObject = JSONObject.parseObject(advancedGeneral);
        JSONArray result = jsonObject.getJSONArray("result");
        int rootCount = 0;
        int keywordCount = 0;
        for (int i = 0; i < result.size(); i++) {
            if ("人物-人物特写".equals(result.getJSONObject(i).get("root"))) {
//                System.out.println(i+":"+result.getJSONObject(i).get("root"));
                rootCount++;
            }
            if (((String)result.getJSONObject(i).get("keyword")).contains("身份证") || "个人证件".equals(result.getJSONObject(i).get("keyword"))) {
//                System.out.println(i+":"+result.getJSONObject(i).get("keyword"));
                keywordCount++;
            }
        }
        if (rootCount > 0 && keywordCount > 0) {
            long end = System.currentTimeMillis();
            System.err.println(end-start);
            System.out.println("手持身份证校验合格！rootCount:"+rootCount+",keywordCount:"+keywordCount+"!!!");
        }
    }
}
