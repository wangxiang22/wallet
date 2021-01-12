package com.xinlian.admin.biz.service;

import com.xinlian.common.request.MessageContentReceiveReq;
import com.xinlian.common.response.ResponseResult;

/**
 * <p>
 * 消息内容表 服务类
 * </p>
 *
 * @author lt
 * @since 2020-06-13
 */
public interface AdminMessageContentService {
    /**
     * 新增消息内容（部分消息新增的同时发送给用户）
     * @param req
     * @return
     */
    ResponseResult createMessageContentReceive(MessageContentReceiveReq req);
}
