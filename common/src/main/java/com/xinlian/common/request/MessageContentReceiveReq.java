package com.xinlian.common.request;

import lombok.Data;

@Data
public class MessageContentReceiveReq {
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 消息类型 - 1：账户消息，2：活动消息，3：系统消息
     */
    private Integer typeCode;
    /**
     * 缩略图地址
     */
    private String thumbnail;
    /**
     * 活动消息详情链接
     */
    private String hyperlink;
    /**
     * 消息接收方 - 三种情况，-1：全部用户，一个uid：一个用户，多个uid用英文逗号拼接：指定用户组
     */
    private String uids;
    /**
     * 消息内容id
     */
    private Long messageId;
    /**
     * 消息接收方类型 - 1：全部用户，2：一个用户，3：一组用户
     */
    private Integer roleType;
}
