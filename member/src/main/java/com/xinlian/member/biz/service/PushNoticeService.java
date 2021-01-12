package com.xinlian.member.biz.service;

import com.xinlian.common.enums.JPushTitleMessageEnum;
import com.xinlian.common.request.PushNoticeReq;
import com.xinlian.common.response.ResponseResult;

public interface PushNoticeService {

    /**
     * 查询推送的结束时间、有值的uid列表或者有值的节点id列表
     * @param pushNoticeReq 需要唯一标识码
     */
    ResponseResult findNoticeEndTime(PushNoticeReq pushNoticeReq);

    /**
     * 三方推送内容记录
     * @param uid uid
     * @param jid 极光id
     * @param currencyCode 币种code
     * @param amount 交易币种数量
     * @param jPushTitleMessageEnum 推送枚举
     * @return
     */
    void saveAppNoticePushRecord(Long uid,String jid, String currencyCode, String amount, JPushTitleMessageEnum jPushTitleMessageEnum);



}
