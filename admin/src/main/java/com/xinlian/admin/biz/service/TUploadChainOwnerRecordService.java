package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TUploadChainOwnerRecord;

import java.util.List;

/**
 * <p>
 * 上传链权人信息记录表 服务类
 * </p>
 *
 * @author 代码生成
 * @since 2020-01-14
 */
public interface TUploadChainOwnerRecordService  {

    int batchInsert(List<TUploadChainOwnerRecord> list);

    /**
     * 获取符合条件的集合
     * @return
     */
    void doDisposeChainOwnerData();
}
