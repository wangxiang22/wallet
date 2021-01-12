//package wallet.service;
//
//import com.xinlian.biz.dao.TWalletInfoMapper;
//import com.xinlian.biz.model.TWalletInfo;
//import com.xinlian.biz.utils.AdminOptionsUtil;
//import com.xinlian.common.dto.LotteryDrawDto;
//import com.xinlian.common.enums.AdminOptionsBelongsSystemCodeEnum;
//import com.xinlian.common.response.BootScreenRes;
//import com.xinlian.common.response.ResponseResult;
//import com.xinlian.member.biz.service.LotteryDrawService;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import wallet.service.base.BaseServiceTest;
//
//
//
//public class LotteryDrawTest extends BaseServiceTest {
//    @Autowired
//    private LotteryDrawService lotteryDrawService;
//    @Autowired
//    private TWalletInfoMapper tWalletInfoMapper;
//    @Autowired
//    AdminOptionsUtil adminOptionsUtil;
//
//    @Test
//    public void test() {
//        TWalletInfo tWalletInfo = new TWalletInfo();
//        tWalletInfo.setUid(491l);
//        tWalletInfo.setCurrencyId(6l);//cat
//        TWalletInfo tWalletInfo1 = tWalletInfoMapper.selectOne(tWalletInfo);
//        System.out.println(tWalletInfo1);
//    }
//}
