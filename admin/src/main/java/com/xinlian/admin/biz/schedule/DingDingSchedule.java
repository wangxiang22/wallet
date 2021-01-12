package com.xinlian.admin.biz.schedule;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.xinlian.admin.biz.service.HomePageService;
import com.xinlian.admin.server.vo.response.IndexInfoDataResponse;
import com.xinlian.biz.dao.CurrencyBalanceHourChangeMapper;
import com.xinlian.common.scedule.AbstractSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Slf4j
@Configuration
@EnableScheduling
@Component
public class DingDingSchedule extends AbstractSchedule {

 @Autowired
 private HomePageService homePageService;


 @Autowired
 private CurrencyBalanceHourChangeMapper currencyBalanceHourChangeMapper;

    @Value("${dingDing}")
    private boolean dingDing;

    @Override
    protected String getCron() {
        return "0 05 12,18,23 * * ?";
        //return "0/20 * * * * ?";
    }

    @Override
    public void doSchedule() {
        if (dingDing){
            return;
        }
        Date date = new Date(System.currentTimeMillis());
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        try {
            IndexInfoDataResponse indexData = homePageService.getIndexData(true);
            //所有已激活矿机用户总数
            String activateTotalValue = indexData.getActivateTotalValue();
            //累计自然用户总数
            String grandTotalIdNosValue = indexData.getGrandTotalIdNosValue();
            //累计注册总数
            String grandTotalRegisterValue = indexData.getGrandTotalRegisterValue();
            //今日已激活矿机用户总数
            String todayActivateValue = indexData.getTodayActivateValue();
            //今日自然用户总数
            String todayGrandIdNosValue = indexData.getTodayGrandIdNosValue();
            //今日新增注册总数
            String todayGrandRegisterValue = indexData.getTodayGrandRegisterValue();

            //获取平台币种总额
            Map<String, String> statisticsCurrencyNum = homePageService.getStatisticsCurrencyNum();
            String usdt = statisticsCurrencyNum.get("USDT");
            String cat = statisticsCurrencyNum.get("CAT");
            String cag = statisticsCurrencyNum.get("CAG");

           //获取今日usdt充值数量
            Map<String, String> map = homePageService.getPlatformWalletData(true, "USDT");
            String todayUsdt = map.get("today");

            BigDecimal catOldBalance =  currencyBalanceHourChangeMapper.getOldCatBalance();
            BigDecimal cagOldBalance =currencyBalanceHourChangeMapper.getOldCagBalance();

            BigDecimal cagBalance = currencyBalanceHourChangeMapper.getCagBalance();
            BigDecimal catBalance = currencyBalanceHourChangeMapper.getCatBalance();

            BigDecimal   nowCagBalance = cagBalance.subtract(cagOldBalance);
            BigDecimal   nowCatBalance = catBalance.subtract(catOldBalance);

         //机器人的webHook地址
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=b8a8207f49c95794cd4accf87a88a68a42209ad93484ebb30dd7a73070d3eed2");
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        //类型为文本
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("截止"+format+"，哥伦布用户数据：\n" +
                "1、注册用户总数："+grandTotalRegisterValue+"【今日新增："+todayGrandRegisterValue+"】\n" +
                "2、自然用户总数："+grandTotalIdNosValue+"【今日新增："+todayGrandIdNosValue+"】\n" +
                "3、激活用户总数："+activateTotalValue+"【今日新增："+todayActivateValue+"】\n" +
                "==============\n" +
                "截止"+format+"，哥伦布币种余额数据：\n" +
                "1、USDT总数："+usdt+"【今日充值："+todayUsdt+"】\n" +
                "2、CAT总数："+cat+"【较上次变化："+nowCatBalance+"】\n" +
                "3、CAG总数："+cag+"【较上次变化："+nowCagBalance+"】@18356411995");
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        //给指定手机号发信息，手机号在内容中写出来
        at.setAtMobiles(Arrays.asList("18356411995"));
        //是否@所有人
        at.setIsAtAll(false);
        request.setAt(at);
            OapiRobotSendResponse response = client.execute(request);
            if (response.isSuccess()){
                log.info("钉钉发送消息成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            log.info("钉钉发送消息失败");
        }
    }


}
