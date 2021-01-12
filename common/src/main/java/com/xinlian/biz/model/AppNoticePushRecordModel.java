package com.xinlian.biz.model;

import lombok.Data;

/**
 * @author Song
 * @date 2020-05-12 16:37
 * @description
 */
@Data
public class AppNoticePushRecordModel {

    //主键id
    private Long id;
    //推送标题
    private String pushTitle;
    //推送内容
    private String pushContent;
    //推送结果
    private Integer pushResult;
    //推送-接收者uid
    private Long pushUid;
    //三方绑定jid
    private String pushJid;



}
