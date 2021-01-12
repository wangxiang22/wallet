package com.xinlian.admin.biz.service;

import com.xinlian.admin.biz.service.base.PageBaseService;
import com.xinlian.biz.dao.OperationLogMapper;
import com.xinlian.biz.model.OperationLogModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * com.xinlian.admin.biz.service
 *
 * @author by Song
 * @date 2020/2/19 12:20
 */
@Service
@Slf4j
public class OperationLogService extends PageBaseService<OperationLogModel> {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLogModel> query(OperationLogModel model) throws Exception {
        return operationLogMapper.query(model);
    }

    public int save(OperationLogModel operationLogModel){
        return operationLogMapper.insert(operationLogModel);
    }
}
