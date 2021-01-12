//
//package wallet;
//
//import com.xinlian.biz.dao.TServerNodeMapper;
//import com.xinlian.biz.model.TServerNode;
//import com.xinlian.biz.model.TUserWithdrawAddressRef;
//import com.xinlian.biz.model.TWalletInfo;
//import com.xinlian.common.result.BizException;
//import com.xinlian.common.result.ErrorInfoEnum;
//import com.xinlian.member.biz.service.TUserWithdrawAddressRefService;
//import com.xinlian.member.server.controller.handler.WalletInfoHandler;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = com.xinlian.MemberApplication.class)
//public class MemberApplicationTest {
//
//    @Autowired
//    private TUserWithdrawAddressRefService userWithdrawAddressRefService;
//    @Autowired
//    private TServerNodeMapper serverNodeMapper;
//    @Test
//    public void test(){
//        TUserWithdrawAddressRef whereModel = new TUserWithdrawAddressRef();
//        whereModel.setToCurrencyAddress("asdas123123123");
//        whereModel.setIsDel(1);
//        TUserWithdrawAddressRef userWithdrawAddressRef = userWithdrawAddressRefService.getByCriteria(whereModel);
//        System.err.println(userWithdrawAddressRef.toString());
//    }
//
//    @Test
//    public void testServerNode(){
//        TWalletInfo walletInfo = new TWalletInfo();
//        walletInfo.setCurrencyAddress("asdas123123123");
//        walletInfo.setUid(1l);
//        TServerNode serverNode = serverNodeMapper.getServerNodeByWithdrawAddress(walletInfo);
//        System.err.println(serverNode.toString());
//    }
//    @Autowired
//    private WalletInfoHandler walletInfoHandler;
//    @Test
//    public void testServerNode1(){
//        TUserWithdrawAddressRef userWithdrawAddressRef = new TUserWithdrawAddressRef();
//        //userWithdrawAddressRef.setToCurrencyAddress("asdas123123123");
//        userWithdrawAddressRef.setUid(1l);
//        //walletInfoHandler.serverNodeJudgeFromUId(1l,"","");
//
//    }
//
//    @Test
//    public void throwTest(){
//        try {
//            if (1 == 1) {
//                throw new BizException(ErrorInfoEnum.PARAM_ERR);
//            }//异常
//        }catch (BizException e){
//            System.err.println(e.getMsg());
//        }
//    }
//}
//
