package com.xinlian.member.biz.service;

import com.xinlian.biz.model.TUdunRequestLog;

/**
 * <p>
 * 优盾请求日志表 服务类
 * </p>
 *
 * @since 2019-12-23
 */
public interface TUdunRequestLogService{

    /**
     * 保存实体对象
     * @param udunRequestLog
     * @return
     */
    int saveModel(TUdunRequestLog udunRequestLog);
}
