
package wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.utils.CommonUtil;
import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import org.databene.contiperf.PerfTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.controller.base.BaseControllerTest;

public class RedisDistributedControllerTest extends BaseControllerTest {


    //个人账户提币单元测试

@Autowired
private RedisClient redisClient;
    @Test
    @PerfTest(invocations = 500,threads = 30)
    public void redisShareInfoTest()throws Exception{
        String url = "/system/v1/withdraw";
        //String paramsJsonStr = "{\"address\":\"0x37f355d50b1272d4d51709bb2fa6c7a738e26744\",\"address_id\":\"1\",\"code\":\"1\",\"coin_id\":\"3\",\"deal_psw\":\"1\",\"num\":\"0.021\"}";
        String paramsJsonStr = "{\"userId\":\"8\",\"smsCode\":\"123456\",\"address\":\"0x37f355d50b1272d4d51709bb2fa6c7a738e26744\",\"address_id\":\"8\",\"coin_id\":\"3\",\"deal_psw\":\"dhhhhhjjjj\",\"num\":\"0.1\",\"uid\":\"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMTA5IiwiOCIsIjg1ZTA0MTgwNTYxNzc5ZTR1bmtub3duIl0sImV4cCI6MTU3NzU3MzA4N30.TicVkOu5OEPufM5AIndnK8chR0mjyuZFK5da0LXUxVM\"}";
        ResponseResult responseResult = postRequestToBaseResponse(url,paramsJsonStr, ResponseResult.class);
        System.err.println(JSONObject.toJSONString(responseResult));
    }

    @Autowired
    private LuaScriptRedisService luaScriptRedisService;


    @Test
    //@PerfTest(invocations = 50,threads = 15)
    public void redisTest(){
        String code = "test000101";
        String redisKey = "IP:LIKE:012012121";

        System.err.println(luaScriptRedisService.doHashIncr(redisKey,code, CommonUtil.getTheDayResidueSecond(),1l));

        //System.err.println("长度:"+redisClient.hashHlen(redisKey));
        //System.err.println("hashKey值:"+luaScriptRedisService.doHashIncr(redisKey,code,0l,0l));
    }

    @Test
    public void aa(){
        redisClient.deleteByKey("ADMIN_OPTION_ORDER_TIME_OUT");
    }
}

