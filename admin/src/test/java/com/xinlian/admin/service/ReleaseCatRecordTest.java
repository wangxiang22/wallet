package com.xinlian.admin.service;

import com.xinlian.admin.biz.service.TReleaseCatRecordService;
import com.xinlian.admin.service.base.BaseServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class ReleaseCatRecordTest extends BaseServiceTest {


    @Autowired
    private TReleaseCatRecordService releaseCatRecordService;




    @Test
    @Transactional
    public void releaseCatTest(){
        /**
        CompletableFuture.supplyAsync(()->{
            println("supplyAsync");
            return groupByReleaseCatNum(); //获取分组后的数值，方便组装sql
        }).thenApplyAsync(result->{
            println("thenApplyAsync(result->)");
            result.forEach(value->{
                println("result.forEach");
                //获取待解仓信息集合
                List<TReleaseCatRecord> lists = getReleaseCatRecordList(value);
                batchDisposeSubtractLock(lists,value);
                batchDisposeWalletInfo(lists,value);
            });
            return true;
        }).whenComplete((v,error)->{
            println("whenComplete");
        }).join();
         */
        println("result.forEach");
        //获取待解仓信息集合
        releaseCatRecordService.timingTaskReleaseCatRecord();
    }

    public void println(String msg){
        System.err.println(Thread.currentThread().getName()+":"+msg);
    }
}
