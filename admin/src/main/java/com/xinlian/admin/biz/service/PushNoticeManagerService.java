package com.xinlian.admin.biz.service;

import com.xinlian.biz.model.TPushNotice;
import com.xinlian.common.request.PageReq;
import com.xinlian.common.response.PageResult;
import com.xinlian.common.response.ResponseResult;

import java.util.List;

public interface PushNoticeManagerService {
    /**
     * 新增定时推送
     */
    ResponseResult createPushNotice(TPushNotice tPushNotice);

    /**
     * 查询全部推送通知信息（分页）
     */
    PageResult<List<TPushNotice>> findPushNoticeListPage(PageReq pageReq);

    /**
     * 提前下线推送通知
     * @param id 推送通知的主键id
     */
    ResponseResult deletePushNotice(Long id);
}
