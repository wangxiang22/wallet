package com.xinlian.biz.dao;

import com.xinlian.biz.model.AliYunEmailConfigModel;
import com.xinlian.biz.model.AliyunEmailLogModel;
import org.springframework.stereotype.Component;

/**
 * com.xinlian.biz.dao
 *
 * @date 2020/2/9 19:28
 */
@Component
public interface AliYunEmailConfigMapper {

    /**
     * 获取当天下一个在用的email账号信息
     * @param lastModel
     * @return
     */
    AliYunEmailConfigModel nextUseSortEmail(AliYunEmailConfigModel lastModel);

    int threadSaveEmailLog(AliyunEmailLogModel emailLogModel);
}
