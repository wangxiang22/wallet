//package wallet.service;
//
//import com.xinlian.rabbitMq.BaseMqProducer;
//import com.xinlian.rabbitMq.UUIDUtil;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import wallet.service.base.BaseServiceTest;
//
//import static com.xinlian.rabbitMq.exchange.ExchangeConstants.GOODS_EXCHANGE;
//import static com.xinlian.rabbitMq.routingKey.RoutingKeyConstants.UPDATE_STOCK_ROUTE;
//
//public class RabbitMqTet extends BaseServiceTest {
//    @Autowired
//    private BaseMqProducer baseMqProducer;
//
//    @Test
//    public void a() {
//        baseMqProducer.sendMessage(GOODS_EXCHANGE, UPDATE_STOCK_ROUTE, "123", null, UUIDUtil.get32UpperCaseUUID());
//    }
//}
