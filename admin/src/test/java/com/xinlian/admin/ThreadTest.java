package com.xinlian.admin;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.xinlian.admin.biz.service.HomePageService;
import com.xinlian.admin.server.vo.response.IndexInfoDataResponse;
import com.xinlian.common.utils.ListUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThreadTest {

    @Test
    public void test1() throws Exception{
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 200000; i++) {
            list.add(i);
        }
        int num = list.size() % 950 == 0 ? list.size() / 950 : list.size() / 950 + 1;
        List<List<Integer>> subLists = ListUtil.averageAssign(list, num);
        for (int j = 0; j < subLists.size(); j++) {

            if(j!=0 && j%100==0) {
                Thread thread = new MySleepThread();
                thread.start();
                thread.join();
            }
            System.err.println(System.currentTimeMillis() + "~~~~~~"+Thread.currentThread().getName()+"--线程!"+j);
        }
    }

     class MySleepThread extends Thread {

        public void run() {
            try {
                Thread.sleep(1000*10L);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Test
    public void test2() throws ParseException {
        String requestDate = "20200610";
        Date date = (new SimpleDateFormat("yyyyMMdd")).parse(requestDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,1);
        date =cal.getTime();
        String format = (new SimpleDateFormat("yyyyMMdd")).format(date);
        System.out.println(format);

        String mat = new SimpleDateFormat("yyyyMMdd").format(new Date());
        System.out.println(mat);
    }


}
