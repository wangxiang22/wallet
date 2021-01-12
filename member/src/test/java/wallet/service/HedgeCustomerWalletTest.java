package wallet.service;

import com.google.gson.Gson;
import com.xinlian.member.biz.service.impl.THedgeCustomerWalletServiceImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wallet.service.base.BaseServiceTest;

public class HedgeCustomerWalletTest extends BaseServiceTest {
    @Autowired
    private THedgeCustomerWalletServiceImpl hedgeCustomerWalletService;

    @Test
    public void test() {
        hedgeCustomerWalletService.updateFreezeStatus();
    }

    @Test
    public void test01(){
        String json = "{'type':'1','num':'asd'}";
        Bean bean = new Gson().fromJson(json, Bean.class);
        System.out.println(": " + bean.type);

//        Map<String,Object> objectMap = JSON.parseObject(json);
//        Integer result = (Integer) objectMap.get("type");
//        System.out.println(result);
    }

    static class Bean{
        int type;
        String num;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }
    }
}
