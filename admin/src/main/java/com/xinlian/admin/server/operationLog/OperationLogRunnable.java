package com.xinlian.admin.server.operationLog;


import com.xinlian.admin.biz.service.OperationLogService;
import com.xinlian.biz.model.OperationLogModel;

public class OperationLogRunnable implements Runnable {


    private OperationLogService operationLogService;

    private String[] params = new String[9];


    public OperationLogRunnable() {
    }

    public OperationLogRunnable(OperationLogService operationLogService,String[] params) {
        this.params = params;
        this.operationLogService = operationLogService;
    }

    @Override
    public void run() {
        try {
            //饿汉式单例获取操作日志model
            OperationLogModel ope =
                    getOperationLogModel(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
            operationLogService.save(ope);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private OperationLogModel getOperationLogModel(
             String modulecode, String typeStr, String UserId,
             String UserLoginName, String requestParamsJSON,
             String responseParamsJSON, String resultJSON,
             String logLevel,String opeDesc) {
        OperationLogModel ope = OperationLogModelSingleton.getInstance();
        ope.setOpeModule(modulecode);
        ope.setOpeType(typeStr);
        ope.setOpeUserid(UserId);
        ope.setOpeUsername(UserLoginName);
        ope.setOpeDesc(opeDesc);
        ope.setOpeReqParam(requestParamsJSON);
        ope.setOpeRespParam(responseParamsJSON);
        ope.setOpeResult(resultJSON);
        ope.setOperationLogLevel(logLevel);
        return ope;
    }


}
