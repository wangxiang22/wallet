package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.dao.TUdunRequestLogMapper;
import com.xinlian.biz.model.TUdunRequestLog;
import com.xinlian.member.biz.service.TUdunRequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优盾请求日志表 服务实现类
 * </p>
 *
 * @since 2019-12-23
 */
@Service
public class TUdunRequestLogServiceImpl implements TUdunRequestLogService {

    @Autowired
    private TUdunRequestLogMapper udunRequestLogMapper;

    @Override
    public int saveModel(TUdunRequestLog udunRequestLog) {
        return udunRequestLogMapper.saveModel(udunRequestLog);
    }
}
