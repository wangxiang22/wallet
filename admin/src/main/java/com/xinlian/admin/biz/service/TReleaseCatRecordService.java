package com.xinlian.admin.biz.service;

import java.util.Map;

/**
 * <p>
 * 释放cat记录表 服务类
 * </p>
 *
 * @author 插件生成
 * @since 2020-01-13
 */
public interface TReleaseCatRecordService {

    void batchInsert(Map<String,Object> paramMap);

    /**
     * 定时任务 - 释放cat
     */
    void timingTaskReleaseCatRecord();

    /**
     * 事务统一处理入口
     */
    void transactionDispose();
}
