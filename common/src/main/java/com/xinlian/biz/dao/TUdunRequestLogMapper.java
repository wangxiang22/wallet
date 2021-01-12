package com.xinlian.biz.dao;

import com.xinlian.biz.model.TUdunRequestLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public interface TUdunRequestLogMapper extends BaseMapper<TUdunRequestLog> {

    int saveModel(TUdunRequestLog udunRequestLog);
}
