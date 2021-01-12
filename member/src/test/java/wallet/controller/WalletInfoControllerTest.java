
package wallet.controller;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.biz.model.Address;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.udun.UdunConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.controller.base.BaseControllerTest;

public class WalletInfoControllerTest extends BaseControllerTest {


    //个人账户提币单元测试
    @Test
    public void shareInfoTest()throws Exception{
        String url = "/system/v1/withdraw";
        //String paramsJsonStr = "{\"address\":\"0x37f355d50b1272d4d51709bb2fa6c7a738e26744\",\"address_id\":\"1\",\"code\":\"1\",\"coin_id\":\"3\",\"deal_psw\":\"1\",\"num\":\"0.021\"}";
        String paramsJsonStr = "{\"userId\":\"8\",\"smsCode\":\"123456\",\"address\":\"0xc52bbd49a10cfba78c36379e8b2267e656430478\",\"address_id\":\"8\",\"coin_id\":\"3\",\"deal_psw\":\"a123456\",\"num\":\"-0.1\",\"uid\":\"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMTA5IiwiOCIsIjg1ZTA0MTgwNTYxNzc5ZTR1bmtub3duIl0sImV4cCI6MTU3NzU3MzA4N30.TicVkOu5OEPufM5AIndnK8chR0mjyuZFK5da0LXUxVM\"}";
        ResponseResult responseResult = postRequestToBaseResponse(url,paramsJsonStr, ResponseResult.class);
        System.err.println(JSONObject.toJSONString(responseResult));
    }

    //公司账户提币单元测试
    @Test
    public void shareInfoCompanyTest()throws Exception{
        String url = "/v3/system/v1/withdraw";
        String paramsJsonStr = "{\"userId\":\"1435340\",\"serverNodeId\":\"109\",\"address\":\"0xc52Bcd49a10cfba78c36379e8b2267e656430478\",\"address_id\":\"1\",\"code\":\"1\",\"coin_id\":\"5\",\"deal_psw\":\"a123456\",\"num\":\"0.121\"}";
        ResponseResult responseResult = postRequestToBaseResponse(url,paramsJsonStr, ResponseResult.class);
        System.err.println(JSONObject.toJSONString(responseResult));
    }


    /**
     * 内部转账Test
     * @throws Exception
     */
    @Test
    public void internalTransferTest()throws Exception{
        String url = "/system/v1/withdraw";
        String paramsJsonStr = "{\"serverNodeId\":\"109\",\"address\":\"0xf910aede85e8161b3a9c673041209270a0135e02\",\"userId\":\"1435340\",\"uid\":\"1435340\",\"address_id\":\"1\",\"code\":\"1\",\"coin_id\":\"6\",\"deal_psw\":\"123456\",\"num\":\"0.01\"}";
        ResponseResult responseResult = postRequestToBaseResponse(url,paramsJsonStr, ResponseResult.class);
        System.err.println(JSONObject.toJSONString(responseResult));
    }

    @Autowired
    private UdunConfig udunConfig;
    @Test
    public void createAddressTest()throws Exception{
        String url = "/system/v1/createAddress";
        String paramsJsonStr = "{\"c_key\":\"sign_key_2019\",\"coinType\":\"60\"}"; //60 ETH - USDT（ETH代币） 发送代币合约地址
        if("do".equals(udunConfig.getTimingTaskFlag())) {
            Address address = postRequestToBaseResponse(url, paramsJsonStr, Address.class);
            System.err.println(JSONObject.toJSONString(address));
        }
        System.err.println(udunConfig.getTimingTaskFlag());
    }


    @Test
    public void callbackTest()throws Exception{
        String url = "/system/v1/udun/callback";
        String paramsJsonStr = "{\"sign\":\"a5e8d467fe001074f0a4482f43a84e69\",\"body\":\"{\"address\":\"0x58a1efba5e6880853023762c85cb49b7460440f7\",\"amount\":\"1000000\",\"blockHigh\":\"10174136\",\"businessId\":\"4239429\",\"coinType\":\"0xdac17f958d2ee523a2206206994597c13d831ec7\",\"decimals\":\"6\",\"fee\":\"2043371200000000\",\"mainCoinType\":\"60\",\"memo\":\"\",\"status\":3,\"tradeId\":\"716781808812818432\",\"tradeType\":2,\"txId\":\"0xb9b4cdc93bba81b0e63a63b7aa49a14910f6e8857b03a65d36de542bd0ed0e8d\"}\",\"nonce\":\"AZe7Na\",\"timestamp\":\"1590935926\"}";
        String address = postRequestToBaseResponse(url,paramsJsonStr, String.class);
        System.err.println(JSONObject.toJSONString(address));
    }

    @Test
    public void allocationCurrencyAddressTest()throws Exception{
        String url = "/system/v1/allocationCurrencyAddress";
        String paramsJsonStr = "";
        String result = getRequestToBaseResponse(url,paramsJsonStr,String.class);
        System.err.println(JSONObject.toJSONString(result));
    }

    @Autowired
    private RedisClient redisClient;
    @Test
    public void getRedisClient(){
        String key = "18756981670";
        redisClient.set(key,112);
        System.err.println(redisClient.get(key).toString());
    }
}

