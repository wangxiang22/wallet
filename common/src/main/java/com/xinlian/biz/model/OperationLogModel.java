package com.xinlian.biz.model;

import lombok.Data;

/**
 * com.xinlian.biz.model
 *
 * @author by Song
 * @date 2020/2/19 11:53
 */
@Data
public class OperationLogModel {

    public static final String PARAMS = "pageNum:到哪页,pageSize:每页数量," +
            "operationLogTimeStartDate:查询开始日期，operationLogTimeEndDate：查询结束日期，" +
            "opeType:请选择日志类型 请传中文-[SYSTEM_LOGIN(1, \"系统登录\"),\n" +
            "    REAL_NAME_AUDIT(2, \"实名审核\"),\n" +
            "    USER_FREEZE(3, \"用户冻结\"),\n" +
            "    WITHDRAW_AUDIT(4, \"提现审核\"),\n" +
            "    SERVER_NODE_ASSERT(5, \"节点维护\"),\n" +
            "    OTHER_OPERATE(6, \"其他操作\")]";

    private Long id;

    private String opeType;

    //操作模块
    private String opeModule;

    //用户名称
    private String opeUsername;

    //用户操作时间
    private String opeTime;

    //操作描述
    private String opeDesc;

    //请求参数
    private String opeReqParam;

    //响应参数
    private String opeRespParam;

    //用户id
    private String opeUserid;

    //返回结果
    private String opeResult;

    //日志级别
    private String operationLogLevel;

    private String operationLogTimeStartDate;

    private String operationLogTimeEndDate;



}
