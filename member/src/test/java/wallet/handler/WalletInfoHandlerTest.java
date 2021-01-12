package wallet.handler;

import com.xinlian.biz.model.TServerNode;
import com.xinlian.common.enums.CurrencyEnum;
import com.xinlian.member.server.controller.handler.WalletInfoHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

public class WalletInfoHandlerTest extends BaseServiceTest {

    @Autowired
    private WalletInfoHandler walletInfoHandler;

    @Test
    public void testUsdt(){
        TServerNode serverNode = new TServerNode();
        serverNode.setCashUsdtStatus(1);
        int getValue = walletInfoHandler.getServerNodeCashStatus(serverNode, CurrencyEnum.USDT.getCurrencyCode());
        System.err.println(getValue);
    }

    @Test
    public void testCat(){
        TServerNode serverNode = new TServerNode();
        serverNode.setRechargeUsdtStatus(1);
        int getValue = walletInfoHandler.getServerNodeCashStatus(serverNode, CurrencyEnum.USDT.getCurrencyCode());
        System.err.println(getValue);
    }

    @Test
    public void testCag(){
        TServerNode serverNode = new TServerNode();
        serverNode.setCashUsdtStatus(1);
        int getValue = walletInfoHandler.getServerNodeCashStatus(serverNode, CurrencyEnum.USDT.getCurrencyCode());
        System.err.println(getValue);
    }
}
