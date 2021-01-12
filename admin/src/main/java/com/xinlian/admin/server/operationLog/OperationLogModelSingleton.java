package com.xinlian.admin.server.operationLog;


import com.xinlian.biz.model.OperationLogModel;

/**
 * 饿汉式单例
 */
public class OperationLogModelSingleton {
    private static OperationLogModel instance = new OperationLogModel();

    private OperationLogModelSingleton() {
    }

    public static OperationLogModel getInstance() {
        return instance;
    }
}
