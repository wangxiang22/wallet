package com.xinlian.biz.dao;

import com.xinlian.biz.model.OperationLogModel;

import java.util.List;

public interface OperationLogMapper {

    int insert(OperationLogModel operationLogModel);

    List<OperationLogModel> query(OperationLogModel operationLogModel);
}
