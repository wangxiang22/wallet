package com.xinlian.common.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


@Slf4j
public class TestCallable implements Callable<List<String>>{

    private List<String> strLists;

    public TestCallable(){
    }

    public TestCallable(List<String> strList){
        this.strLists = strList;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public List<String> call() throws Exception {
        List<String> resultList = new ArrayList<>();
        strLists.forEach(string -> {
            String result = Thread.currentThread().getName()+"线程任务："+string;
            resultList.add(result);
            log.info(result);
        });
        return resultList;
    }

}

