package wallet.handler;

import com.xinlian.member.server.controller.handler.MaleChainWithdrawHandler;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

public class MaleChainWithdrawHandlerTest extends BaseServiceTest {

    @Autowired
    private MaleChainWithdrawHandler maleChainWithdrawHandler;

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();
    //公司账户提币单元测试
    @Test
    @PerfTest(threads = 10, duration = 15)
    public void maleChainWithdrawHandlerTest(){
        maleChainWithdrawHandler.doSubClassTask();
    }

}
