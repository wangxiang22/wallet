package wallet.service;

import com.xinlian.member.biz.service.CallInterfaceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

/**
 * wallet.service
 *
 * @author by Song
 * @date 2020/8/3 20:51
 */
public class CallInterfaceServiceTest extends BaseServiceTest {


    @Autowired
    private CallInterfaceService callInterfaceService;


    @Test
    public void testPostGo()throws Exception{
        String url = "";
        String param = " {\n" +
                "     \"address\": \"0xE36Ea790bc9d7AB70C55260C66D52b1eca985f84\",\n" +
                "     \"offset\": 0,\n" +
                "     \"limit\": 10,\n" +
                "     \"order\": \"id\"\n" +
                " }";
        String response = callInterfaceService.callInterface(param,url,String.class);
        System.err.println(response);
    }
}
