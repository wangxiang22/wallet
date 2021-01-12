package com.xinlian.biz.dao;

import com.xinlian.biz.model.RequestRecordLogModel;
import org.springframework.stereotype.Component;

/**
 * @author Song
 * @date 2020-07-31 15:38
 * @description
 */
@Component
public interface RequestRecordLogMapper {


    int threadSaveRequestRecordLog(RequestRecordLogModel requestRecordLogModel);
}
