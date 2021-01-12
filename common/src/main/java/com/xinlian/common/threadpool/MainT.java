package com.xinlian.common.threadpool;

import com.alibaba.fastjson.JSONObject;
import com.xinlian.common.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class MainT {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        main_test1();
        main_test2();
        main_test3();
    }

    private static void main_test1() {
        try {

            //按照线程池最大线程数5，拆分集合
            List<List<String>> splitSource = ListUtil.averageAssign(getSourceData(1), ThreadPoolProxyFixed.FIXED_THREAD_POOL_SIZE);
            log.info(JSONObject.toJSONString(splitSource));
            //拆分集合，平均分批提交线程池执行
            splitSource.forEach(splitEvery -> {
                ThreadPoolProxyFixed.getThreadPoolProxy().submitCallable(new TestCallable(splitEvery));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            afterThreadPool();

        }
    }

    /**
     * 线程池结束调用
     * 处理返回结果
     */
    private static void afterThreadPool() {
        try {
            //一定要关闭，关闭线程池再次提交需要重新创建（自动）
            ThreadPoolProxyFixed.getThreadPoolProxy().shutdown();
            //获取线程池返回结果
            List<List<String>> results = ThreadPoolProxyFixed.getThreadPoolProxy().getFutureFormatResult();
            results.forEach(result->{
                log.info(Thread.currentThread().getName() + "_RESULT_" + JSONObject.toJSONString(result));
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static void main_test3() {
        try {
            //按照线程池最大线程数5，拆分集合
            List<List<String>> splitSource = ListUtil.averageAssign(getSourceData(3), ThreadPoolProxyFixed.FIXED_THREAD_POOL_SIZE);
            log.info(JSONObject.toJSONString(splitSource));
            //拆分集合，平均分批提交线程池执行
            splitSource.forEach(splitEvery -> {
                ThreadPoolProxyFixed.getThreadPoolProxy().submitCallable(new TestCallable(splitEvery));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            afterThreadPool();

        }
    }

    private static void main_test2() {
        try {
            //按照线程池最大线程数5，拆分集合
            List<List<String>> splitSource = ListUtil.averageAssign(getSourceData(2), ThreadPoolProxyFixed.FIXED_THREAD_POOL_SIZE);
            log.info(JSONObject.toJSONString(splitSource));
            //拆分集合，平均分批提交线程池执行
            splitSource.forEach(splitEvery -> {
                ThreadPoolProxyFixed.getThreadPoolProxy().submitCallable(new TestCallable(splitEvery));
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            afterThreadPool();

        }
    }

    private static List<String> getSourceData(int i) {
        List<String> sources = new ArrayList<>();
        sources.add("1" + i);
        sources.add("2" + i);
        sources.add("3" + i);
        sources.add("4" + i);
        sources.add("5" + i);
        sources.add("6" + i);
        sources.add("7" + i);
        sources.add("8" + i);
        sources.add("9" + i);
        sources.add("10" + i);
        sources.add("11" + i);
        sources.add("12" + i);
        sources.add("13" + i);
        sources.add("14" + i);
        sources.add("15" + i);
        sources.add("16" + i);
        sources.add("17" + i);
        sources.add("18" + i);
        sources.add("19" + i);
        sources.add("20" + i);
        sources.add("21" + i);
        sources.add("22" + i);
        sources.add("23" + i);
        sources.add("24" + i);
        sources.add("25" + i);
        sources.add("26" + i);
        sources.add("27" + i);
        sources.add("28" + i);
        sources.add("29" + i);
        sources.add("30" + i);
        sources.add("33" + i);
        return sources;
    }
}
