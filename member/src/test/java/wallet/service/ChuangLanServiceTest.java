package wallet.service;

import com.xinlian.member.biz.chuanglan.util.ChuangLanSmsService;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

/**
 * wallet.service
 *
 * @author by Song
 * @date 2020/6/20 23:37
 */
public class ChuangLanServiceTest extends BaseServiceTest {

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();
    @Autowired
    private ChuangLanSmsService chuangLanSmsService;



    @Test
    public void sendSmsTest(){
        String phone = "85215801525748";
        String code = "139821";
        //chuangLanSmsService.sendRegisterCodeInte(phone, code);
    }

    public static void main(String[] args) {
        String pa = "/^1[3456789]\\d{9}$/";
        String pa1 = "^1\\d{10}$";
        String mobile = "158015-5748";
        String email = "ddsf@qq.com";
        System.err.println(email
                .matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"));
        System.err.println(mobile.matches(pa1));
    }
}
