package wallet.service;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import wallet.service.base.BaseServiceTest;

/**
 * @author Song
 * @date 2020-07-15 09:14
 * @description
 */
public class PostUrlRequestTest extends BaseServiceTest {

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void callTest()throws Exception{
        String url = "";
        String [] array = new String[]{"apipro.columbu.world",
                "hsdfdhfg.columbu.world",
                "sdfjhbdfg.columbu.world",
                "ewjsdfbgdgf.columbu.world",
                "ghfkj.columbu.world" };
        while(true){
            for (int i = 0; i < array.length; i++) {
                url = "https://"+array[i] + "/wallet/spot/checkTime";
                String result = restTemplate.getForObject(url,String.class);
                //result
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(200!=jsonObject.getIntValue("code")){
                    System.err.println("请求url:"+url+"返回结果："+result);
                }
            }

        }

    }


}
