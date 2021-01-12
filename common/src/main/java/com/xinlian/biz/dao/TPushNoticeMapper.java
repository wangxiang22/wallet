package com.xinlian.biz.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xinlian.biz.model.AppNoticePushRecordModel;
import com.xinlian.biz.model.TPushNotice;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 推送通知表 Mapper 接口
 * </p>
 *
 * @author wjf
 * @since 2020-03-09
 */
@Component
public interface TPushNoticeMapper extends BaseMapper<TPushNotice> {

    /**
     * 三方推送内容记录
     * @param noticePushRecordModel
     * @return
     */
    int appNoticePushRecord(AppNoticePushRecordModel noticePushRecordModel);

}
