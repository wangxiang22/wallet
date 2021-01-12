package wallet.handler;

import com.xinlian.member.server.controller.handler.MaleChainCallbackHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

/**
 * @author Song
 * @date 2020-06-12 10:18
 * @description 提币回调单元测试
 */
public class WithdrawCallbackHandlerTest extends BaseServiceTest {

    @Autowired
    private MaleChainCallbackHandler maleChainCallbackHandler;

    /**
     * 提币回调单元测试
     */
    @Test
    public void withdrawCallback(){
        String data = "{\"tx_hash\":\"0x9a34cd6ac6116a432395372f4875efdccba55b13d25164f87574d7a2bd3ede2e\",\"businessId\":4302879,\"status\":1,\"fee\":758850957617935}";
        String result = maleChainCallbackHandler.doWithdrawCallbackMethod(data);
        System.err.println(result);
    }
}
