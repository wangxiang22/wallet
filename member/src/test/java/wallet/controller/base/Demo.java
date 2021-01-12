/*
package wallet.controller.base;

import com.xinlian.MemberApplication;
import com.xinlian.member.biz.redis.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MemberApplication.class)
public class Demo {

@Autowired
private RedisClient redisClient;

    @Test
    public void demo(){
        String string="http://mainapp.columbu.world/index.php?s=faxian&c=api&m=findShow&id=51";
        StringBuffer stringBuffer=new StringBuffer(string);
        String substring1 = stringBuffer.substring(stringBuffer.indexOf("?"));
        System.out.println(substring1);
        String re=  "http://www.fffff.ssss"+substring1;
        Object o = redisClient.get("98767890");
        System.out.println(o);
        System.out.println(re);
    }


}

*/
