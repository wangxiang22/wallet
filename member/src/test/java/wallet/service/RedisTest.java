package wallet.service;

import com.xinlian.member.biz.redis.LuaScriptRedisService;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.redis.RedisConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import wallet.service.base.BaseServiceTest;

public class RedisTest extends BaseServiceTest {
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LuaScriptRedisService luaScriptRedisService;
    @Test
    public void aaa(){
        //redisClient.getHash("test","10997");
//        UserInfoDto userInfoDto = redisClient.getHash("userState1", "23644");
//        System.out.println(userInfoDto);
        redisClient.set("redis-cl1","12309",300);
        String getValue = redisClient.get("redis-cl1");
        System.err.println(getValue);
    }

    @Test
    public void setAddRank(){
        String redisKey = RedisConstant.APP_REDIS_PREFIX + "USER_INVITATION_CODE_3312";

        String member = "USER_INVITATION_CODE025551_";
        //double k = redisClient.zincrementScore(redisKey,member,0.5);
        //Long k = luaScriptRedisService.doZSetIncr(redisKey,member,1000L,1);
        double ke = redisClient.zincrementScore(redisKey,member,1,10002L);

        double ke1 = redisClient.zincrementScore(redisKey,member.concat("253"),1,10002L);
        System.err.println(ke);
        System.err.println(ke1);
    }

    public static void main(String[] args) {
        System.err.println(Double.MAX_VALUE);
        System.err.println(Long.MAX_VALUE);
        System.err.println(Integer.MAX_VALUE);
    }

    @Test
    public void b()throws Exception{
        String lockIpRedisKey = "ADMIN_WALLET_".concat("LOCK_IP");

        double reqIpRank = redisClient.zincrementScore(lockIpRedisKey,"1200-102-01",0.0,12933L);

        Long rank = redisClient.zrank(lockIpRedisKey,"1200-102-01");

        System.err.println(reqIpRank);

        System.err.println("排名:" + (rank + 1));
    }
}
